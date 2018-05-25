#include <string.h>
#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <sys/time.h>
#include <algorithm>

#include "baseUtil.h"
#include "ZkWrapper.h"

pthread_once_t ZkWrapper::new_instance_once_ = PTHREAD_ONCE_INIT;

ZkWrapper& ZkWrapper::GetInstance() {
	pthread_once(&new_instance_once_, NewInstance);
	return GetClient();
}

void ZkWrapper::NewInstance() {
	GetClient();
}

ZkWrapper& ZkWrapper::GetClient() {
	static ZkWrapper singleton;
	return singleton;
}

ZkWrapper::ZkWrapper()
	: zhandle_(NULL), log_fp_(NULL), expired_handler_(DefaultSessionExpiredHandlerImp),  user_context_(NULL),
        session_state_(ZOO_CONNECTING_STATE), session_check_running_(false) {
        pthread_mutex_init(&state_mutex_, NULL);
        pthread_cond_init(&state_cond_, NULL);
}

ZkWrapper::~ZkWrapper() {
	if (session_check_running_) { // 终止会话检测线程
		session_check_running_ = false; // 设置后,子线程会退出
		pthread_join(session_check_tid_, NULL);//等待子线程SessionCheckThreadMain退出
	}
	if (zhandle_) {
		zookeeper_close(zhandle_);
	}
	if (log_fp_) {
		fclose(log_fp_);
	}
	pthread_cond_destroy(&state_cond_);
	pthread_mutex_destroy(&state_mutex_);
}

string ZkWrapper::fullPath(const string node)
{
	if (startsWith(node,root+ZK_PATH_SEP) || node==root) {
			return node;
	}
	string tempPath = root + ZK_PATH_SEP + node;
	
	string path = toks_to_path(tokenize_path(tempPath,ZK_PATH_SEP),ZK_PATH_SEP);
	
	return path;
}

bool ZkWrapper::Init(const string host, int timeout, const string rootPath,
									   int redoTimes,int sleepTime,
									   SessionExpiredHandler expired_handler, void* context,
										 bool debug, const string& zklog) {
	// 用户配置
	this->session_timeout_ = timeout;
	this->root             = rootPath;
	this->constr           = host;
	this->redoTimes        = redoTimes;
	this->sleepTime        = sleepTime;
	
	if (expired_handler) {
		this->expired_handler_ = expired_handler;
	}
	this->user_context_ = context;
	// log级别
	ZooLogLevel log_level = debug ? ZOO_LOG_LEVEL_DEBUG : ZOO_LOG_LEVEL_INFO;
	zoo_set_debug_level(log_level);
	// log目录
	if (!zklog.empty()) {
		log_fp_ = fopen(zklog.c_str(), "w");
		if (!log_fp_) {
			return false;
		}
		zoo_set_log_stream(log_fp_);
	}
	// zk初始化，除非参数有问题，否则总是可以立即返回
	//
	// 存在一个非常罕见的BUG场景：就是zookeeper_init返回赋值到zhandle_之前就完成了到
	// zookeeper的连接并回调了SessionWatcher，所以在SessionWatcher里一定要注意不要依赖
	// zhandle_，而是使用SessionWatcher被传入的zhandle参数。
	zhandle_ = zookeeper_init(host.c_str(), SessionWatcher, timeout, NULL, this, 0);
	if (!zhandle_) {
		#ifdef _DEBUG_
		  cout<<"zookeeper_init err !"<<__FILE__<<__LINE__<<endl;
		#endif
		return false;
	}
	/*
	 * 等待session初始化完成，两种可能返回值：
	 * 1，连接成功，会话建立。
	 * 2，会话过期，在初始化期间很难发生，场景：连接成功后io线程cpu卡住，导致zkserver一段时间没有收到心跳，会话超时了，
	 * 然后cpu恢复运转，connected的watch事件开始处理更新了session_state然后cond_signal，然而init线程cpu卡住了，
	 * 然后zkserver挂了，然后io线程重连了另外一个zkserver，然而session过期了，然后返回我们session_expire的，然后
	 * session_expire的watch事件更新了session_state，然后init线程cpu正常了，然后看见了expire_session这个状态。
	 * （天哪，真的有这种牛逼的巧合吗。。。只是比较严谨而已~）
	 */
	pthread_mutex_lock(&state_mutex_);
	while (session_state_ != ZOO_CONNECTED_STATE && session_state_ != ZOO_EXPIRED_SESSION_STATE) {
		pthread_cond_wait(&state_cond_, &state_mutex_);
	}
	int session_state = session_state_;
	pthread_mutex_unlock(&state_mutex_);
	if (session_state == ZOO_EXPIRED_SESSION_STATE) { // 会话过期，fatal级错误
		#ifdef _DEBUG_
		  cout<<"zookeeper session EXPIRED !"<<__FILE__<<__LINE__<<endl;
		#endif
		return false;
	}
	/*
	 * 会话建立，可以启动一个zk状态检测线程，主要是发现2种问题：
	 *	1，处于session_expire状态，那么回调SessionExpiredHandler，由用户终结程序（zkserver告知我们会话超时）。
	 *	2，处于非connected状态，那么判断该状态持续时间是否超过了session timeout时间，
	 *	超过则回调SessionExpiredHandler，由用户终结程序（client自己意识到会话超时）。
	 */
	session_check_running_ = true;
	zk_active = true;
	pthread_create(&session_check_tid_, NULL, SessionCheckThreadMain, this);
	return true;
}

void ZkWrapper::GetNodeDataCompletion(int rc, const char* value, int value_len,
        															const struct Stat* stat, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZCLOSING);

	const ZKWatchContext* watch_ctx = (const ZKWatchContext*)data;

	if (rc == ZOK) {
		watch_ctx->getnode_handler(kZKSucceed, watch_ctx->path, value, value_len, watch_ctx->context);
		if (!watch_ctx->watch) { // 没有注册watch
			delete watch_ctx;
		}
		return;
	}
	if (rc == ZNONODE) {
		watch_ctx->getnode_handler(kZKNotExist, watch_ctx->path, value, value_len, watch_ctx->context);
	} else {
		watch_ctx->getnode_handler(kZKError, watch_ctx->path, value, value_len, watch_ctx->context);
	}
	// 只要不是ZOK，那么zk都不会触发Watch事件了
	delete watch_ctx;
}

void ZkWrapper::GetNodeWatcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx) {
	assert(type == ZOO_DELETED_EVENT || type == ZOO_CHANGED_EVENT
			|| type == ZOO_NOTWATCHING_EVENT || type == ZOO_SESSION_EVENT);

	ZKWatchContext* context = (ZKWatchContext*)watcher_ctx;

	if (type == ZOO_SESSION_EVENT) { // 跳过会话事件,由zk handler的watcher进行处理
		return;
	}

	if (type == ZOO_DELETED_EVENT) {
		context->getnode_handler(kZKDeleted, context->path, NULL, 0, context->context);
		delete context;
	} else {
		if (type == ZOO_CHANGED_EVENT) {
			int rc = zoo_awget(zh, context->path.c_str(), GetNodeWatcher, context, GetNodeDataCompletion, context);
			if (rc == ZOK) {
				return;
			}
		} else if (type == ZOO_NOTWATCHING_EVENT) {
			// nothing to do
		}
		context->getnode_handler(kZKError, context->path, NULL, 0, context->context);
		delete context;
	}
}

bool ZkWrapper::GetNode(const string path, GetNodeHandler handler, void* context, bool watch) {

	watcher_fn watcher = watch ? GetNodeWatcher : NULL;

	ZKWatchContext* watch_ctx = new ZKWatchContext(path, NULL, context, this, watch);
	watch_ctx->getnode_handler = handler;

	int rc = zoo_awget(zhandle_, path.c_str(), watcher, watch_ctx, GetNodeDataCompletion, watch_ctx);
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_awget err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return false;
	}	
	return true;
}

bool ZkWrapper::GetChildren(const string path, GetChildrenHandler handler, void* context, bool watch) {

	watcher_fn watcher = watch ? GetChildrenWatcher : NULL;

	ZKWatchContext* watch_ctx = new ZKWatchContext(path, NULL, context, this, watch);
	watch_ctx->getchildren_handler = handler;

	int rc = zoo_awget_children(zhandle_, path.c_str(), watcher, watch_ctx, GetChildrenStringCompletion, watch_ctx);
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_awget_children err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return false;
	}	
	return true;
}

void ZkWrapper::GetChildrenStringCompletion(int rc, const struct String_vector* strings, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZCLOSING);

	const ZKWatchContext* watch_ctx = (const ZKWatchContext*)data;

	if (rc == ZOK) {
		watch_ctx->getchildren_handler(kZKSucceed, watch_ctx->path, strings->count, strings->data, watch_ctx->context);
		if (!watch_ctx->watch) { // 没有注册watch
			delete watch_ctx;
		}
		return;
	}
	if (rc == ZNONODE) {
		watch_ctx->getchildren_handler(kZKNotExist, watch_ctx->path, 0, NULL, watch_ctx->context);
	} else {
		watch_ctx->getchildren_handler(kZKError, watch_ctx->path, 0, NULL, watch_ctx->context);
	}
	// 只要不是ZOK，那么zk都不会触发Watch事件了
	delete watch_ctx;
}

void ZkWrapper::GetChildrenWatcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx) {
	assert(type == ZOO_DELETED_EVENT || type == ZOO_CHILD_EVENT
			|| type == ZOO_NOTWATCHING_EVENT || type == ZOO_SESSION_EVENT);

	ZKWatchContext* context = (ZKWatchContext*)watcher_ctx;

	if (type == ZOO_SESSION_EVENT) { // 跳过会话事件,由zk handler的watcher进行处理
		return;
	}

	if (type == ZOO_DELETED_EVENT) {
		context->getchildren_handler(kZKDeleted, context->path, 0, NULL, context->context);
		delete context;
	} else {
		if (type == ZOO_CHILD_EVENT) {
			int rc = zoo_awget_children(zh, context->path.c_str(), GetChildrenWatcher, context, GetChildrenStringCompletion, context);
			if (rc == ZOK) {
				return;
			}
		} else if (type == ZOO_NOTWATCHING_EVENT) {
			// nothing to do
		}
		context->getchildren_handler(kZKError, context->path, 0, NULL, context->context);
		delete context;
	}
}


void ZkWrapper::ChildrenListen(const string path, bool* changeTag,ChildrenListenHandler handler) {

	ChildWatchContext* watch_ctx = new ChildWatchContext(path, changeTag,this);
	watch_ctx->handler = handler;

	int rc = zoo_awget_children(zhandle_, path.c_str(), ChildrenListen_Watcher, watch_ctx, ChildrenListen_Completion, watch_ctx);
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_awget_children err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
	}
}

void ZkWrapper::ChildrenListen_Completion(int rc, const struct String_vector* strings, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZCLOSING);

	ChildWatchContext* watch_ctx = (ChildWatchContext*)data;

	if (rc == ZOK) {
		watch_ctx->handler(kZKSucceed, watch_ctx->path, watch_ctx->valueTag);
		return;
	}
	if (rc == ZNONODE) {
		watch_ctx->handler(kZKNotExist, watch_ctx->path, watch_ctx->valueTag);
	} else {
		#ifdef _DEBUG_
		  cout<<"ChildrenListen_Completion err_code:"<<rc<<endl;
		#endif
		watch_ctx->handler(kZKError, watch_ctx->path,watch_ctx->valueTag);
	}
	// 只要不是ZOK，那么zk都不会触发Watch事件了
	delete watch_ctx;
}

void ZkWrapper::ChildrenListen_Watcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx) {
	assert(type == ZOO_DELETED_EVENT || type == ZOO_CHILD_EVENT
			|| type == ZOO_NOTWATCHING_EVENT || type == ZOO_SESSION_EVENT);

	ChildWatchContext* context = (ChildWatchContext*)watcher_ctx;

	if (type == ZOO_SESSION_EVENT) { // 跳过会话事件,由zk handler的watcher进行处理
		return;
	}

	if (type == ZOO_DELETED_EVENT) {
		context->handler(kZKDeleted, context->path, context->valueTag);
		delete context;
	} else {
		if (type == ZOO_CHILD_EVENT) {
			int rc = zoo_awget_children(zh, context->path.c_str(), ChildrenListen_Watcher, context, ChildrenListen_Completion, context);
			if (rc == ZOK) {
				return;
			}
		} else if (type == ZOO_NOTWATCHING_EVENT) {
			// nothing to do
		}
		
		#ifdef _DEBUG_
		  cout<<"ChildrenListen_Watcher type:"<<type<<" path:"<<path<<" context->path:"<<context->path<<endl;
		#endif
		context->handler(kZKError, context->path, context->valueTag);
		delete context;
	}
}

//-----------------

void ZkWrapper::ChildrenListen2(const string path, 
																set<string>* fileTypeListForTask,
																set<string>* pathListForListen,
																map<string,bool>* watcherMap,
																ChildrenListenHandler2 handler) {

	ChildWatchContext2* watch_ctx = new ChildWatchContext2(path,fileTypeListForTask,pathListForListen,watcherMap,this);
	watch_ctx->handler = handler;

	int rc = zoo_awget_children(zhandle_, path.c_str(), ChildrenListen2_Watcher, watch_ctx, ChildrenListen2_Completion, watch_ctx);
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_awget_children err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
	}
}

void ZkWrapper::ChildrenListen2_Completion(int rc, const struct String_vector* strings, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZCLOSING);

	ChildWatchContext2* watch_ctx = (ChildWatchContext2*)data;

	if (rc == ZOK) {
		watch_ctx->handler(kZKSucceed, strings->count, strings->data, watch_ctx);
		return;
	}
	if (rc == ZNONODE) {
		watch_ctx->handler(kZKNotExist, 0,NULL,watch_ctx);
	} else {
		watch_ctx->handler(kZKError, 0,NULL,watch_ctx);
	}
	// 只要不是ZOK，那么zk都不会触发Watch事件了
	delete watch_ctx;
}

void ZkWrapper::ChildrenListen2_Watcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx) {
	assert(type == ZOO_DELETED_EVENT || type == ZOO_CHILD_EVENT
			|| type == ZOO_NOTWATCHING_EVENT || type == ZOO_SESSION_EVENT);

	ChildWatchContext2* context = (ChildWatchContext2*)watcher_ctx;

	if (type == ZOO_SESSION_EVENT) { // 跳过会话事件,由zk handler的watcher进行处理
		return;
	}

	if (type == ZOO_DELETED_EVENT) {
		context->handler(kZKDeleted, 0,NULL,context);
		delete context;
	} else {
		if (type == ZOO_CHILD_EVENT) {
			int rc = zoo_awget_children(zh, context->path.c_str(), ChildrenListen2_Watcher, context, ChildrenListen2_Completion, context);
			if (rc == ZOK) {
				return;
			}
		} else if (type == ZOO_NOTWATCHING_EVENT) {
			// nothing to do
		}
		context->handler(kZKError, 0,NULL,context);
		delete context;
	}
}


//---------------------



bool ZkWrapper::Exist(const string path, ExistHandler handler, void* context, bool watch) {

	watcher_fn watcher = watch ? ExistWatcher : NULL;

	ZKWatchContext* watch_ctx = new ZKWatchContext(path, NULL, context, this, watch);
	watch_ctx->exist_handler = handler;

	int rc = zoo_awexists(zhandle_, path.c_str(), watcher, watch_ctx, ExistCompletion, watch_ctx);
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_awexists err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return false;
	}	
	return true;
}

void ZkWrapper::ExistCompletion(int rc, const struct Stat* stat, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZCLOSING);

	const ZKWatchContext* watch_ctx = (const ZKWatchContext*)data;

	if (rc == ZOK || rc == ZNONODE) {
		watch_ctx->exist_handler(rc == ZOK ? kZKSucceed : kZKNotExist, watch_ctx->path, stat, watch_ctx->context);
		if (!watch_ctx->watch) { // 没有注册watch
			delete watch_ctx;
		}
		return;
	}
	watch_ctx->exist_handler(kZKError, watch_ctx->path, NULL, watch_ctx->context);
	// 只要不是ZOK，那么zk都不会触发Watch事件了
	delete watch_ctx;
}

void ZkWrapper::ExistWatcher(zhandle_t* zh, int type, int state, const char* path, void* watcher_ctx) {
	assert(type == ZOO_DELETED_EVENT || type == ZOO_CREATED_EVENT || type == ZOO_CHANGED_EVENT
			|| type == ZOO_NOTWATCHING_EVENT || type == ZOO_SESSION_EVENT);

	ZKWatchContext* context = (ZKWatchContext*)watcher_ctx;

	if (type == ZOO_SESSION_EVENT) { // 跳过会话事件,由zk handler的watcher进行处理
		return;
	}

	if (type == ZOO_NOTWATCHING_EVENT) {
		context->exist_handler(kZKError, context->path, NULL, context->context);
	} else if (type == ZOO_DELETED_EVENT) {
		context->exist_handler(kZKDeleted, context->path, NULL, context->context);
	} else if (type == ZOO_CREATED_EVENT || type == ZOO_CHANGED_EVENT) { // 节点创建或者元信息变动,重新获取通知用户
		int rc = zoo_awexists(zh, context->path.c_str(), ExistWatcher, context, ExistCompletion, context);
		if (rc == ZOK) {
			return;
		}
		context->exist_handler(kZKError, context->path, NULL, context->context);
	}
	delete context;
}

void ZkWrapper::NodeListen_Exist(const string path,map<string, bool>* watcherMap, const string fileType, 
																	NodeListenHandler handler) {

	NodeWatchContext* watch_ctx = new NodeWatchContext(path, fileType, watcherMap,this);
	watch_ctx->handler = handler;

	int rc = zoo_awexists(zhandle_, path.c_str(), NodeListen_ExistWatcher, watch_ctx, NodeListen_ExistCompletion, watch_ctx);

	if(rc != ZOK && rc != ZNONODE){
		#ifdef _DEBUG_
		  cout<<"node not exist , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
	}
}

void ZkWrapper::NodeListen_ExistCompletion(int rc, const struct Stat* stat, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZCLOSING);

	NodeWatchContext* watch_ctx = (NodeWatchContext*)data;

	if (rc == ZOK || rc == ZNONODE) {
		watch_ctx->handler(rc == ZOK ? kZKSucceed : kZKNotExist, watch_ctx);
		return;
	}
	watch_ctx->handler(kZKError, watch_ctx);
	// 只要不是ZOK，那么zk都不会触发Watch事件了
	delete watch_ctx;
}

void ZkWrapper::NodeListen_ExistWatcher(zhandle_t* zh, int type, int state, const char* path, void* watcher_ctx) {
	assert(type == ZOO_DELETED_EVENT || type == ZOO_CREATED_EVENT || type == ZOO_CHANGED_EVENT
			|| type == ZOO_NOTWATCHING_EVENT || type == ZOO_SESSION_EVENT);

	NodeWatchContext* context = (NodeWatchContext*)watcher_ctx;

	if (type == ZOO_SESSION_EVENT) { // 跳过会话事件,由zk handler的watcher进行处理
		return;
	}

	if (type == ZOO_NOTWATCHING_EVENT) {
		context->handler(kZKError, context);
	} else if (type == ZOO_DELETED_EVENT) {
		context->handler(kZKDeleted, context);
	} else if (type == ZOO_CREATED_EVENT || type == ZOO_CHANGED_EVENT) {
		int rc = zoo_awexists(zh, context->path.c_str(),NodeListen_ExistWatcher, context, NodeListen_ExistCompletion, context);
		if (rc == ZOK) {
			context->handler(kZKSucceed, context);
			return;
		}
		context->handler(kZKError, context);
	}
	delete context;
}


bool ZkWrapper::Create(const string path, const string value, int flags, CreateHandler handler, void* context) {

	ZKWatchContext* watch_ctx = new ZKWatchContext(path, NULL,context, this, false);
	watch_ctx->create_handler = handler;

	int rc = zoo_acreate(zhandle_, path.c_str(), value.c_str(), value.length(), 
	                     &ZOO_OPEN_ACL_UNSAFE, flags, CreateCompletion, watch_ctx);
	return rc == ZOK ? true : false;
		
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_acreate err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return false;
	}	
	return true;
}

void ZkWrapper::CreateCompletion(int rc, const char* value, const void* data) {
	assert(rc == ZOK || rc == ZNODEEXISTS || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZNOCHILDRENFOREPHEMERALS || rc == ZCLOSING);

	const ZKWatchContext* watch_ctx = (const ZKWatchContext*)data;
	if (rc == ZOK) {
		watch_ctx->create_handler(kZKSucceed, watch_ctx->path, value, watch_ctx->context);
	} else if (rc == ZNONODE) {
		watch_ctx->create_handler(kZKNotExist, watch_ctx->path, "", watch_ctx->context);
	} else if (rc == ZNODEEXISTS) {
		watch_ctx->create_handler(kZKExisted, watch_ctx->path, "", watch_ctx->context);
	} else {
		watch_ctx->create_handler(kZKError, watch_ctx->path, "", watch_ctx->context);
	}
	delete watch_ctx;
}

bool ZkWrapper::Set(const string path, const string value, SetHandler handler, void* context) {

	ZKWatchContext* watch_ctx = new ZKWatchContext(path, NULL, context, this, false);
	watch_ctx->set_handler = handler;

	int rc = zoo_aset(zhandle_, path.c_str(), value.c_str(), value.length(), -1, SetCompletion, watch_ctx);
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_aset err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return false;
	}	
	return true;
}

void ZkWrapper::SetCompletion(int rc, const struct Stat* stat, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT || rc == ZBADVERSION ||
			rc == ZNOAUTH || rc == ZNONODE || rc == ZCLOSING);

	const ZKWatchContext* watch_ctx = (const ZKWatchContext*)data;
	if (rc == ZOK) {
		watch_ctx->set_handler(kZKSucceed, watch_ctx->path, stat, watch_ctx->context);
	} else if (rc == ZNONODE) {
		watch_ctx->set_handler(kZKNotExist, watch_ctx->path, NULL, watch_ctx->context);
	} else {
		watch_ctx->set_handler(kZKError, watch_ctx->path, NULL, watch_ctx->context);
	}
	delete watch_ctx;
}

bool ZkWrapper::Delete(const string path, DeleteHandler handler, void* context) {
	
	ZKWatchContext* watch_ctx = new ZKWatchContext(path, NULL, context, this, false);
	watch_ctx->delete_handler = handler;

	int rc = zoo_adelete(zhandle_, path.c_str(), -1, DeleteCompletion, watch_ctx);
	
	if(rc != ZOK){
		#ifdef _DEBUG_
		  cout<<"zoo_adelete err , path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return false;
	}	
	return true;
}

void ZkWrapper::DeleteCompletion(int rc, const void* data) {
	assert(rc == ZOK || rc == ZCONNECTIONLOSS || rc == ZOPERATIONTIMEOUT || rc == ZBADVERSION ||
				 rc == ZNOAUTH || rc == ZNONODE || rc == ZNOTEMPTY || rc == ZCLOSING);

	const ZKWatchContext* watch_ctx = (const ZKWatchContext*)data;
	if (rc == ZOK) {
		watch_ctx->delete_handler(kZKSucceed, watch_ctx->path, watch_ctx->context);
	} else if (rc == ZNONODE) {
		watch_ctx->delete_handler(kZKNotExist, watch_ctx->path, watch_ctx->context);
	} else if (rc == ZNOTEMPTY) {
		watch_ctx->delete_handler(kZKNotEmpty, watch_ctx->path, watch_ctx->context);
	} else {
		watch_ctx->delete_handler(kZKError, watch_ctx->path, watch_ctx->context);
	}
	delete watch_ctx;
}

ZKErrorCode ZkWrapper::GetNode(const string path, char* buffer, int* buffer_len, GetNodeHandler handler,
		void* context, bool watch) {
	watcher_fn watcher = watch ? GetNodeWatcher : NULL;

	ZKWatchContext* watch_ctx = NULL;
	if (watch) {
		watch_ctx = new ZKWatchContext(path, NULL, context, this, watch);
		watch_ctx->getnode_handler = handler;
	}
	int rc = zoo_wget(zhandle_, path.c_str(), watcher, watch_ctx, buffer, buffer_len, NULL);
	if (rc == ZOK) {
		return kZKSucceed;
	} else if (rc == ZNONODE) {
		#ifdef _DEBUG_
		  cout<<"zoo_wget err-node not exist,path: "<<path<<endl;
		#endif
		return kZKNotExist;
	}else{
		#ifdef _DEBUG_
		  cout<<"zoo_wget err,path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return kZKError;
	}
}

ZKErrorCode ZkWrapper::GetChildren(const string path, vector<string> &value, GetChildrenHandler handler,
		void* context, bool watch) {
	watcher_fn watcher = watch ? GetChildrenWatcher : NULL;

	ZKWatchContext* watch_ctx = NULL;
	if (watch) {
		watch_ctx = new ZKWatchContext(path, NULL, context, this, watch);
		watch_ctx->getchildren_handler = handler;
	}
	struct String_vector strings = { 0, NULL };
	int rc = zoo_wget_children(zhandle_, path.c_str(), watcher, watch_ctx, &strings);
	if (rc == ZOK) {
		for (int i = 0; i < strings.count; ++i) {
			
			string node = strings.data[i];
			value.push_back(node);
		}
		deallocate_String_vector(&strings);
		return kZKSucceed;
	} else if (rc == ZNONODE) {
		#ifdef _DEBUG_
		  cout<<"zoo_wget_children err-node not exist,path: "<<path<<endl;
		#endif
		return kZKNotExist;
	}else{
		#ifdef _DEBUG_
		  cout<<"zoo_wget_children err,path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return kZKError;
	}
}

ZKErrorCode ZkWrapper::Exist(const string path, struct Stat* stat, ExistHandler handler, void* context, bool watch) {
	watcher_fn watcher = watch ? ExistWatcher : NULL;

	ZKWatchContext* watch_ctx = NULL;
	if (watch) {
		watch_ctx = new ZKWatchContext(path, NULL, context, this, watch);
		watch_ctx->exist_handler = handler;
	}
	int rc = zoo_wexists(zhandle_, path.c_str(), watcher, watch_ctx, stat);
	if (rc == ZOK) {
		return kZKSucceed;
	} else if (rc == ZNONODE) {
		#ifdef _DEBUG_
		  cout<<"zoo_wexists err-node not exist,path: "<<path<<endl;
		#endif
		return kZKNotExist;
	}else{
		#ifdef _DEBUG_
		  cout<<"zoo_wexists err,path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return kZKError;
	}
}

ZKErrorCode ZkWrapper::Create(const string path, const string value, int flags, char* path_buffer, int path_buffer_len) {
	int rc = zoo_create(zhandle_, path.c_str(), value.c_str(), value.length(), 
	                    &ZOO_OPEN_ACL_UNSAFE, flags, path_buffer, path_buffer_len);
	if (rc == ZOK) {
		#ifdef _DEBUG_
		  cout<<"zoo_create OK,path: "<<path<<endl;
		#endif
		return kZKSucceed;
	} else if (rc == ZNONODE) {
		#ifdef _DEBUG_
		  cout<<"zoo_create err-ZNONODE,path: "<<path<<endl;
		#endif
		return kZKNotExist;
	} else if (rc == ZNODEEXISTS) {
		#ifdef _DEBUG_
		  cout<<"zoo_create err-ZNODEEXISTS,path: "<<path<<endl;
		#endif
		return kZKExisted;
	}else{
		#ifdef _DEBUG_
		  cout<<"zoo_create err,path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return kZKError;
	}
}

ZKErrorCode ZkWrapper::Set(const string path, const string value) {
	int rc = zoo_set(zhandle_, path.c_str(), value.c_str(), value.length(), -1);
	if (rc == ZOK) {
		#ifdef _DEBUG_
		  cout<<"zoo_set ZOK,path: "<<path<<",value:"<<value<<endl;
		#endif
		return kZKSucceed;
	} else if (rc == ZNONODE) {
		#ifdef _DEBUG_
		  cout<<"zoo_set err-ZNONODE,path: "<<path<<endl;
		#endif
		return kZKNotExist;
	}else{
		#ifdef _DEBUG_
		  cout<<"zoo_set err,path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return kZKError;
	}
}

ZKErrorCode ZkWrapper::Delete(const string path) {
	int rc = zoo_delete(zhandle_, path.c_str(), -1);
	if (rc == ZOK) {
		#ifdef _DEBUG_
		  cout<<"zoo_delete ZOK,path: "<<path<<endl;
		#endif
		return kZKSucceed;
	} else if (rc == ZNONODE) {
		#ifdef _DEBUG_
		  cout<<"zoo_delete err-ZNONODE,path: "<<path<<endl;
		#endif
		return kZKNotExist;
	} else if (rc == ZNOTEMPTY) {
		#ifdef _DEBUG_
		  cout<<"zoo_delete err-ZNOTEMPTY,path: "<<path<<endl;
		#endif
		return kZKNotEmpty;
	}else{
		#ifdef _DEBUG_
		  cout<<"zoo_delete err,path: "<<path<<endl;
		#endif
		THROW_EXCEPTION(__LINE__,rc);
		return kZKError;
	}
}

void ZkWrapper::ExpiredHandlerImp() {
	
	#ifdef _DEBUG_
	  cout<<"ExpiredHandlerImp execute! "<<endl;
	#endif
	
	bool session_active = false;
	//重连
	while(redoTimes>0){
		
		#ifdef _DEBUG_
		  cout<<"try reconnect ! "<<endl;
		#endif
		
		redoTimes--;
		sleep(sleepTime);
		
		if (zhandle_) {
			zookeeper_close(zhandle_);
		}
		
		zhandle_ = zookeeper_init(constr.c_str(), SessionWatcher, session_timeout_, NULL, this, 0);
		if (zhandle_) {
			pthread_mutex_lock(&state_mutex_);
			while (session_state_ != ZOO_CONNECTED_STATE && session_state_ != ZOO_EXPIRED_SESSION_STATE) {
				pthread_cond_wait(&state_cond_, &state_mutex_);
			}
			pthread_mutex_unlock(&state_mutex_);
			if (session_state_ != ZOO_EXPIRED_SESSION_STATE) {
				session_check_running_ = true;
		  	zk_active = true;
		  	session_active = true;
		  	
		  	#ifdef _DEBUG_
				  cout<<"reconnect sucess! "<<endl;
				#endif
				
		  	break;
			}
		}
	}
	//重连失败，执行回调函数
	if(!session_active){
			//if (zkWrapper->session_check_running_) {
			//	zkWrapper->session_check_running_ = false;
			//	pthread_join(zkWrapper->session_check_tid_, NULL);
			//}
			//if (zkWrapper->zhandle_) {
			//	zookeeper_close(zkWrapper->zhandle_);
			//}
			//if (zkWrapper->log_fp_) {
			//	fclose(lzkWrapper->og_fp_);
			//}
			//pthread_cond_destroy(&state_cond_);
			//pthread_mutex_destroy(&state_mutex_);
			
			#ifdef _DEBUG_
			  cout<<"execute expired_handler_ ! "<<endl;
			#endif
			expired_handler_(user_context_);
	}
}

void ZkWrapper::DefaultSessionExpiredHandlerImp(void* context) {
	exit(0);
}

void ZkWrapper::SessionWatcher(zhandle_t *zh, int type, int state, const char *path, void *watcher_ctx) {
	assert(type == ZOO_SESSION_EVENT);

	ZkWrapper* zkWrapper = (ZkWrapper*)watcher_ctx;
	zkWrapper->UpdateSessionState(zh, state);
}

void ZkWrapper::UpdateSessionState(zhandle_t* zhandle, int state) {
	pthread_mutex_lock(&state_mutex_);
	session_state_ = state;
	// 连接建立，记录协商后的会话过期时间，唤醒init函数（只有第一次有实际作用）
	if (state == ZOO_CONNECTED_STATE) {
		session_timeout_ = zoo_recv_timeout(zhandle);
		pthread_cond_signal(&state_cond_);
	} else if (state == ZOO_EXPIRED_SESSION_STATE) {
		// 会话过期，唤醒init函数
		pthread_cond_signal(&state_cond_);
	} else {
		// 连接异常，记录下异常开始时间，用于计算会话是否过期
		session_disconnect_ms_ = GetCurrentMs();
	}
	pthread_mutex_unlock(&state_mutex_);
}

void ZkWrapper::CheckSessionState() {
	while (session_check_running_) {
		bool session_expired = false;
		pthread_mutex_lock(&state_mutex_);
		
		if(session_state_ == ZOO_CONNECTED_STATE){
			if(!zk_active){
				if(reconnect_handler_) //闪断回调函数
				   reconnect_handler_(reconnect_context_); // session 级别回调
			}
			zk_active = true;
		}
		if (session_state_ == ZOO_EXPIRED_SESSION_STATE) {
			session_expired = true;
		} 
		//其它异常情况下，等待，直到超时
		else if (session_state_ != ZOO_CONNECTED_STATE) {
			/*if (GetCurrentMs() - session_disconnect_ms_ > session_timeout_) {
				session_expired = true;
			}*/
			zk_active = false;
		}
		
		pthread_mutex_unlock(&state_mutex_);
		if (session_expired) { // 会话过期，回调用户终结程序
			//结束时，调用回调函数
			return ExpiredHandlerImp(); // 停止检测
		}
		usleep(1000); // 睡眠1毫秒
	}
}

void* ZkWrapper::SessionCheckThreadMain(void* arg) {
	ZkWrapper* zkWrapper = (ZkWrapper*)arg;
	zkWrapper->CheckSessionState();
	return NULL;
}

int64_t ZkWrapper::GetCurrentMs() {
	struct timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

//------------------------------------------------------------------------------

bool ZkWrapper::exist(const string path) {

	string t_fullPath =fullPath(path);		
	int rc = Exist(t_fullPath);

	switch (rc)
	{
		case kZKSucceed:
			return true;
		case kZKNotExist:
			return false;
		default:
			return false;
	}
}

bool ZkWrapper::isTempNodeExist(const string path){
	
	string t_fullPath =fullPath(path);
	struct Stat stat;
	int rc = Exist(t_fullPath,&stat);
	
	switch (rc)
	{
		case kZKSucceed:
			if(stat.ephemeralOwner > 0){
				return true;
			}else{
				return false;
			}
		case kZKNotExist:
			return false;
		default:
			return false;
	}
}

vector<string> ZkWrapper::getChildNodeList(const string path) {
	
	string t_fullPath =fullPath(path);
	
	vector<string> children;
	int rc =  GetChildren(t_fullPath,children);
	
	switch (rc)
	{
		case kZKSucceed:
			break;
		case kZKNotExist:
			children.clear();
			break;
		default:
			children.clear();
			break;
	}
	return children;
}

string ZkWrapper::getNodeValue(const string path) {
	
	string t_fullPath = fullPath(path);
	
	char buffer[1024] = {0};
	int buffer_len = sizeof(buffer);
	
	int rc = GetNode(t_fullPath,buffer,&buffer_len,NULL,NULL,false);
	
	switch (rc)
	{
		case kZKSucceed:
			break;
		case kZKNotExist:
			buffer[0] ='\0';
			break;
		default:
			buffer[0] ='\0';
			break;
	}
	
	string value = buffer;
	return value;
}

bool ZkWrapper::insertNode(const string path, const string value) {
	
	string t_fullPath = fullPath(path);
	
	if(Exist(t_fullPath.c_str())==kZKSucceed){
		int rc = Set(t_fullPath, value);
	
		switch (rc)
		{
			case kZKSucceed:
				return true;
			case kZKNotExist:
				return false;
			default:
				return false;
		}
	}else{
		return createAndUpdate(t_fullPath,value,0);
	}
}

bool ZkWrapper::insertTempNode(const string path, const string value) {
	
	string t_fullPath = fullPath(path);
	return createAndUpdate(t_fullPath,value,ZOO_EPHEMERAL);
}

bool ZkWrapper::createAndUpdate(const string path, const string value,int createMode)
{
	//获取所有的路径
	vector<string> pathVec = toks_to_allPath(tokenize_path(path,ZK_PATH_SEP),ZK_PATH_SEP);
	int size = pathVec.size();
  for (int i = 0; i < size; i++) {
  		string node      = pathVec[i];
  		string nodeValue = (i ==size - 1) ? value:"";
  	
  	  int rc = Exist(node);
			if(rc == kZKSucceed){
				continue;
			}
  	  else if(rc == kZKNotExist){
  	  	 if(Create(node,nodeValue,createMode)!=kZKSucceed)
  	  			return false;
  	  }else{
  	  	return false;
  	  }
  }
  return true;
}

bool ZkWrapper::getAllChildNode(map<int,vector<string> > &t_node_map_tmp,//本次查询的子节点
																map<int,vector<string> > &m_node_map_all)//所有的子节点
{
	if(t_node_map_tmp.empty()){
		return true;
	}
	
	typedef map<int,vector<string> >::value_type valType;
	map<int,vector<string> > t_node_map_new;//新的子节点
	
	for(map<int,vector<string> >::iterator itr_tmpMap =t_node_map_tmp.begin();
		                                     itr_tmpMap!=t_node_map_tmp.end();itr_tmpMap++)
	{
		int t_depth = itr_tmpMap->first;
		vector<string> t_node_tmp = itr_tmpMap->second; //当前深度的节点
		vector<string> t_node_vec_all; //m_node_map_all 中的记录
		vector<string> t_node_vec_new; //t_node_map_new 中的记录
		
		map<int,vector<string> >::iterator iter_pos=m_node_map_all.find(t_depth);   
		if (iter_pos!=m_node_map_all.end())
    { 
     	t_node_vec_all = iter_pos->second;
    }
		
    iter_pos=t_node_map_new.find(t_depth);   
    if (iter_pos!=t_node_map_new.end())
    {
     	t_node_vec_new = iter_pos->second;
    }
		
		for(vector<string>::iterator itr_vec =t_node_tmp.begin();
			                           itr_vec!=t_node_tmp.end();++itr_vec)
		{
			vector<string> children;
			vector<string>::iterator itr_children;
			if( GetChildren((*itr_vec).c_str(), children)!= kZKSucceed ){
				return false;
			}
			
			for(itr_children=children.begin();
					itr_children!=children.end();++itr_children)
			{			
				string childNode = *itr_vec+ZK_PATH_SEP+*itr_children;
				t_node_vec_new.push_back(childNode);
			}
			t_node_vec_all.push_back(*itr_vec);
		}
		
		m_node_map_all.insert(valType(t_depth,t_node_vec_all));
		if(!t_node_vec_new.empty()){
			t_node_map_new.insert(valType(t_depth+1,t_node_vec_new));
		}
	}
	
	t_node_map_tmp.clear();
	t_node_map_tmp.swap(t_node_map_new);
	
	return true;
}

bool ZkWrapper::getAllNode(const string path,
													 map<int,vector<string> > &m_node_map_all)
{
	typedef map<int,vector<string> >::value_type valType;
	
	vector<string> t_root_vec;
	t_root_vec.push_back(path);
	
	vector<string> children;
	if(GetChildren(path.c_str(), children) != kZKSucceed){
		return false;
	}

	if(!children.empty()){
		
		vector<string> t_vec;
		for(vector<string>::iterator iter =children.begin();
			                           iter!=children.end();++iter){
			string node = path + ZK_PATH_SEP +*iter;                      	
			t_vec.push_back(node);
		}
		
		map<int,vector<string> > t_node_map_tmp;
		t_node_map_tmp.insert(valType((int)1,t_vec));
	
		while(!t_node_map_tmp.empty())
		{
			if(!getAllChildNode(t_node_map_tmp,m_node_map_all)){
				return false;
			}
		}
	}
	m_node_map_all.insert(valType((int)0,t_root_vec));
	return true;
}

bool ZkWrapper::deleteNode(const string path) {
	
	string t_fullPath = fullPath(path);

	map<int,vector<string> > t_node_map;
	if(!getAllNode(t_fullPath,t_node_map)){
		return false;
	}

	if(!t_node_map.empty()){

		int size=t_node_map.size();
		for(int i=size-1;i>=0;i--){
			vector<string> t_node_vec = t_node_map[i];
			for(vector<string>::iterator itr_vec =t_node_vec.begin();
				                           itr_vec!=t_node_vec.end();++itr_vec)
			{
				ZKErrorCode rc = Delete((*itr_vec).c_str());
				switch (rc)
				{
					case kZKSucceed:
						break;
					case kZKNotExist:
						continue;
					case kZKNotEmpty:
						return false;
					default:
						return false;
				}
			}
		}
	}
	return true;
}

bool ZkWrapper::deleteTempNode(const string path) {
	
	string t_fullPath =fullPath(path);
	
	ZKErrorCode rc = Delete(t_fullPath);
	
	switch (rc)
	{
		case kZKSucceed:
			return true;
		case kZKNotExist:
			return false;
		case kZKNotEmpty:
			return false;
		default:
			
			return false;
	}
}

bool ZkWrapper::updateNodeValue(const string path, const string value) {
	
	string t_fullPath =fullPath(path);
	
	int rc = Set(t_fullPath,value);
	
	switch (rc)
	{
		case kZKSucceed:
			return true;
		case kZKNotExist:
			return false;
		default:
			return false;
	}
}

void ZkWrapper::THROW_EXCEPTION(const int linenum ,const int rc){

	int errCode = 0;

	switch (rc)
	{
		case ZSYSTEMERROR:
			errCode = ExceptionErrCode::E_ZK_ZSYSTEMERROR;
			break;
		case ZRUNTIMEINCONSISTENCY:
			errCode = ExceptionErrCode::E_ZK_ZRUNTIMEINCONSISTENCY;
			break;
		case ZDATAINCONSISTENCY:
			errCode = ExceptionErrCode::E_ZK_ZDATAINCONSISTENCY;
			break;
		case ZCONNECTIONLOSS:
			errCode = ExceptionErrCode::E_ZK_ZCONNECTIONLOSS;
			break;
		case ZMARSHALLINGERROR:
			errCode = ExceptionErrCode::E_ZK_ZMARSHALLINGERROR;
			break;
		case ZUNIMPLEMENTED:
			errCode = ExceptionErrCode::E_ZK_ZUNIMPLEMENTED;
			break;
		case ZOPERATIONTIMEOUT:
			errCode = ExceptionErrCode::E_ZK_ZOPERATIONTIMEOUT;
			break;
		case ZBADARGUMENTS:
			errCode = ExceptionErrCode::E_ZK_ZBADARGUMENTS;
			break;
		case ZINVALIDSTATE:
			errCode = ExceptionErrCode::E_ZK_ZINVALIDSTATE;
			break;
		case ZAPIERROR:
			errCode = ExceptionErrCode::E_ZK_ZAPIERROR;
			break;
		case ZNONODE:
			errCode = ExceptionErrCode::E_ZK_ZNONODE;
			break;
		case ZNOAUTH:
			errCode = ExceptionErrCode::E_ZK_ZNOAUTH;
			break;
		case ZBADVERSION:
			errCode = ExceptionErrCode::E_ZK_ZBADVERSION;
			break;
		case ZNOCHILDRENFOREPHEMERALS:
			errCode = ExceptionErrCode::E_ZK_ZNOCHILDRENFOREPHEMERALS;
			break;
		case ZNODEEXISTS:
			errCode = ExceptionErrCode::E_ZK_ZNODEEXISTS;
			break;
		case ZNOTEMPTY:
			errCode = ExceptionErrCode::E_ZK_ZNOTEMPTY;
			break;
		case ZSESSIONEXPIRED:
			errCode = ExceptionErrCode::E_ZK_ZSESSIONEXPIRED;
			break;
		case ZINVALIDCALLBACK:
			errCode = ExceptionErrCode::E_ZK_ZINVALIDCALLBACK;
			break;
		case ZINVALIDACL:
			errCode = ExceptionErrCode::E_ZK_ZINVALIDACL;
			break;
		case ZAUTHFAILED:
			errCode = ExceptionErrCode::E_ZK_ZAUTHFAILED;
			break;
		case ZCLOSING:
			errCode = ExceptionErrCode::E_ZK_ZCLOSING;
			break;
		case ZNOTHING:
			errCode = ExceptionErrCode::E_ZK_ZNOTHING;
			break;
		case ZSESSIONMOVED:
			errCode = ExceptionErrCode::E_ZK_ZSESSIONMOVED;
			break;
		default:
			errCode = 0;
	}

	throw ZK_Exception(__FILE__,linenum,errCode,ExceptionErrCode::GetMsgByCode(errCode));
}

//----------------------------------------------------------

bool ZkWrapper::tryLock(string lockName)
{
	try {
		string lockPath=SHARE_LOCK+ZK_PATH_SEP+lockName+ZK_LINK_LETTER;
		
		char buffer[1024] = {0};
		buffer[0] = '\0';
		
		int rc = ZOK;
		//检查share节点是否存在
		rc = zoo_exists(zhandle_,SHARE_LOCK.c_str(), 0,NULL);
		if(rc == ZNONODE)
		{
			rc = zoo_create(zhandle_, SHARE_LOCK.c_str(),NULL, -1,&ZOO_OPEN_ACL_UNSAFE,
                  		0,buffer, sizeof(buffer));
			if(rc != ZOK ){
				return false;
			}
		}else{
			if(rc != ZOK){
				return false;
			}
		}
		
		//创建临时节点	
		buffer[0] = '\0';
		rc = zoo_create(zhandle_, lockPath.c_str(),NULL, -1,&ZOO_OPEN_ACL_UNSAFE,
                  	ZOO_EPHEMERAL | ZOO_SEQUENCE,buffer, sizeof(buffer));
	  if(rc != ZOK ){
			return false;
		}
		currentLockNode = buffer;
		
		//获取所有锁节点
		struct String_vector strings = { 0, NULL };
		rc = zoo_get_children(zhandle_, SHARE_LOCK.c_str(),0, &strings);
	  if(rc != ZOK ){
			return false;
		}
		
		int t_lock_body = lockName.length();
		vector<char*> t_lock_vec;
		
    int32_t i;  
    for(i = 0; i < strings.count; i++)  
    {  
    	char* data = strings.data[i];
    	string s_node = data;
    	string name = s_node.substr(0,t_lock_body);
    	
    	if (name==lockName){
    		t_lock_vec.push_back(const_cast<char*>(s_node.c_str()));
    	}
      deallocate_String(&strings.data[i]);
    }
    free(strings.data);  
    strings.data = 0;

		
		// 若当前节点为最小节点，则获取锁成功	
    sort(t_lock_vec.begin(),t_lock_vec.end());
		
		waitLockNode = SHARE_LOCK+ZK_PATH_SEP+t_lock_vec[0];
		
		t_lock_vec.clear();
		if(waitLockNode==currentLockNode){
			return true;
		}

	} catch (char * e) {
		 printf("%s", e);
     return false;
  }
  return false;
}


bool ZkWrapper::lock(const string lockPath)
{
  return lock(lockPath,connectionTimeoutMs);
}

bool ZkWrapper::lock(const string lockPath,long time)
{
  if (tryLock(lockPath)) {
    return true;
  } else {
    // 等待锁
    if(waitForLock(time)){
    	return true;
    }else{
    	unlock();
    	return false;
    }
  }
}

bool ZkWrapper::waitForLock(long waitTime)
{ 
  long time = 3;
  while(waitTime>0)
  {
  	int rc = zoo_exists(zhandle_,waitLockNode.c_str(), 0,NULL);
  	if( rc == ZNONODE ){
			return true;
		}else if(rc == ZOK){
			time = time < waitTime ? time:waitTime;
		  sleep(time);
		  waitTime-=time;
		}else{
			return false;
		}
	}
  return false;
}

bool ZkWrapper::unlock()
{
	//删除临时节点
	int rc = zoo_delete(zhandle_,currentLockNode.c_str(),0);
	if( rc == ZNONODE ){
		return true;
	}else if(rc == ZOK){
		return true;
	}else{
		return false;
	}
	
}


void ZkWrapper::setListenterNode(const string node,
	                               map<string, bool>* watcherMap, 
	                               const string fileType, 
	                               NodeListenHandler handler)
{
	NodeListen_Exist(node,watcherMap,fileType,handler);
}

void ZkWrapper::setListenterChild(const string node,bool* changeTag, ChildrenListenHandler handler)
{
	ChildrenListen(node,changeTag,handler);
}

void ZkWrapper::setListenterChild(const string path,
																	set<string>* fileTypeListForTask,
																	set<string>* pathListForListen,
																	map<string,bool>* watcherMap,
																	ChildrenListenHandler2 handler)
{
	ChildrenListen2(path,fileTypeListForTask,pathListForListen,watcherMap,handler);
}

void ZkWrapper::setListenterConnect(void* context,
																		SessionExpiredHandler handler)
{
	reconnect_handler_ = handler;
	reconnect_context_ = context;
}

void ZkWrapper::deleteListenterNode(const string node)
{
	
}

bool ZkWrapper::isActive()
{
	return zk_active;
}