#ifndef ZK_WRAPPER_H_
#define ZK_WRAPPER_H_

#include <iostream>
#include <pthread.h>
#include <stdint.h>
#include <string>
#include <map>
#include <vector>
#include <set>
#include "zookeeper.h"
#include "ZkEnum.h"
#include "ExceptionErrCode.h"

/**
 *		对于注册了Watch的操作，严格根据下列返回码来区分watch是否失效。
 *
 *		一旦Watch失效，用户需自行决定是否，以及何时发起新的操作，ZkWrapper内部不会帮用户做决策，
 *		尤其体现在一个操作因为Connection loss或者Connection timeout错误码而失败的情况下，ZkWrapper
 *		也并没有做什么特殊处理，而是直接反馈给用户自己决策是否以及何时发起下一次操作。
 *
 */

class ZkWrapper;

using namespace std;
using namespace ZK;

enum ZKErrorCode {
	kZKSucceed= 0, // 操作成功，watch继续生效
	kZKNotExist,   // 节点不存在，对于exist操作watch继续生效，其他操作均失效
	kZKError,      // 请求失败, watch失效
	kZKDeleted,    // 节点删除，watch失效
	kZKExisted,    // 节点已存在，Create失败
	kZKNotEmpty    // 节点有子节点，Delete失败
};

typedef void (*SessionExpiredHandler)(void* context);
typedef void (*GetNodeHandler)(ZKErrorCode errcode, const string path, const string value, int value_len, void* context);
typedef void (*GetChildrenHandler)(ZKErrorCode errcode, const string path, int count, char** data, void* context);
typedef void (*ExistHandler)(ZKErrorCode errcode, const string path, const struct Stat* stat, void* context);
typedef void (*CreateHandler)(ZKErrorCode errcode, const string path, const string value, void* context);
typedef void (*SetHandler)(ZKErrorCode errcode, const string path, const struct Stat* stat, void* context);
typedef void (*DeleteHandler)(ZKErrorCode errcode, const string path, void* context);


struct ZKWatchContext {
	ZKWatchContext(const string path,const string value, void* context, ZkWrapper* zkWrapper, bool watch){
		this->watch     = watch;
		this->path      = path;
		this->value     = value;
		this->context   = context;
		this->zkWrapper = zkWrapper;
	};

	bool watch;
	void* context;
	string path;
	string value;
	ZkWrapper* zkWrapper;
	union {
		GetNodeHandler getnode_handler;
		GetChildrenHandler getchildren_handler;
		ExistHandler exist_handler;
		CreateHandler create_handler;
		SetHandler set_handler;
		DeleteHandler delete_handler;
	};
}; 

typedef void (*NodeListenHandler)(ZKErrorCode errcode, void* context);

struct NodeWatchContext {
	NodeWatchContext(const string path,const string fileType,map<string, bool>* watcherMap,ZkWrapper* zkWrapper){
		this->path       = path;
		this->fileType   = fileType;
		this->watcherMap = watcherMap;
		this->zkWrapper  = zkWrapper;
	};

	string path;
	string fileType;
	map<string,bool>* watcherMap;
	ZkWrapper* zkWrapper;
	union {
		NodeListenHandler handler;
	};
};

typedef void (*ChildrenListenHandler)(ZKErrorCode errcode, const string path, bool* changeTag);

struct ChildWatchContext {
	ChildWatchContext(const string path,bool* valueTag, ZkWrapper* zkWrapper){
		this->path      = path;
		this->valueTag  = valueTag;
		this->zkWrapper = zkWrapper;
	};

	string path;
	bool* valueTag;
	ZkWrapper* zkWrapper;
	union {
		ChildrenListenHandler handler;
	};
};

typedef void (*ChildrenListenHandler2)(ZKErrorCode errcode,int count, char** data, void* context);

struct ChildWatchContext2 {
	ChildWatchContext2(const string path,
	                   set<string>* fileTypeListForTask,
	                   set<string>* pathListForListen,
	                   map<string,bool>* watcherMap,
	                   ZkWrapper* zkWrapper){

		this->path = path;
		this->fileTypeListForTask = fileTypeListForTask;
		this->pathListForListen   = pathListForListen;
		this->watcherMap = watcherMap;
		this->zkWrapper  = zkWrapper;
	}

	string path;
	set<string>* fileTypeListForTask;
	set<string>* pathListForListen;
	map<string,bool>* watcherMap;
	ZkWrapper* zkWrapper;
	union {
		ChildrenListenHandler2 handler;
	};
};

struct SessionContext {
	SessionContext(const string path,const string value,const string json,ZkWrapper* zkWrapper){
		this->path      = path;
		this->value     = value;
		this->json      = json;
		this->zkWrapper = zkWrapper;
	};

	std::string path;
	std::string value;
	std::string json;
	ZkWrapper* zkWrapper;
};

class ZkWrapper {
	
public:

	ZkWrapper();
	
	~ZkWrapper();
	
	static ZkWrapper& GetInstance();

	bool Init( const string host, int timeout,const string rootPath,
						 int redoTimes = -1,int sleepTime = -1,
					   SessionExpiredHandler expired_handler = NULL, void* context = NULL,
						 bool debug = false, const string& zklog = "");

	/**
	* 判断节点是否存在
	*/
	bool exist(const string path); 
  /**
  * 判断临时节点是否存在
  */
	bool isTempNodeExist(const string path);
  /**
  * 获取子目录
  */
	vector<string> getChildNodeList(const string path);
  /**
  * 获取节点内容
  */
	string getNodeValue(const string path);	
  /**
  * 创建永久节点,并设置消息
  */
	bool insertNode(const string path, const string value ="");
  /**
  * 创建临时节点,并设置消息
  */
	bool insertTempNode(const string path, const string value);
	
	
	bool createAndUpdate(const string path, const string value,int createMode);
		
  /**
  * 删除节点及子目录
  */
	bool deleteNode(const string path);
	/**
   * 删除临时节点
   */
	bool deleteTempNode(const string path);
	/**
   * 设置节点内容
   */
	bool updateNodeValue(const string path, const string value);
		
	/**
	* 加锁
	*/
	bool lock(const string lockPath);
	
	bool tryLock(string lockPath);
	
	bool lock(const string lockPath,const long time);
	
  /**
	* 释放锁
	*/
  bool unlock();	
		
	bool waitForLock(const long waitTime);
	
	/*
	* 节点监听
	*/
	void setListenterNode(const string node,
	                      map<string, bool>* watcherMap, 
		                    const string fileType, 
		                    NodeListenHandler handler);
	
	void setListenterChild(const string node,bool* changeTag, ChildrenListenHandler handler);
	
	void setListenterChild(	const string path,
													set<string>* fileTypeListForTask,
													set<string>* pathListForListen,
													map<string,bool>* watcherMap,
													ChildrenListenHandler2 handler);
	/*
	* 连接监听
	*/
	void setListenterConnect(void* context,SessionExpiredHandler handler);
	
	void deleteListenterNode(const string node);

	bool isActive();
	
private:		
		
	/* async api */
	bool GetNode(const string path, GetNodeHandler handler, void* context, bool watch = false);

	bool GetChildren(const string path, GetChildrenHandler handler, void* context, bool watch = false);
	
	bool Exist(const string path, ExistHandler handler, void* context, bool watch = false);
	
	bool Create(const string path, const string value, int flags, CreateHandler handler, void* context);

	bool Set(const string path, const string value, SetHandler handler, void* context);

	bool Delete(const string path, DeleteHandler handler, void* context);	

	/* sync api */
	ZKErrorCode GetNode(const string path, char* buffer, int* buffer_len, GetNodeHandler handler = NULL,
			void* context = NULL, bool watch = false);

	ZKErrorCode GetChildren(const string path, vector<string> &value, GetChildrenHandler handler = NULL,
			void* context = NULL, bool watch = false);

	ZKErrorCode Exist(const string path, struct Stat* stat = NULL, ExistHandler handler = NULL,
			void* context = NULL, bool watch = false);

	ZKErrorCode Create(const string path, const string value, int flags, char* path_buffer = NULL, int path_buffer_len = 0);

	ZKErrorCode Set(const string path, const string value);

	ZKErrorCode Delete(const string path);

private:
	
  bool getAllChildNode(map<int,vector<string> > &t_node_map_tmp,
											 map<int,vector<string> > &m_node_map_all);
  
  bool getAllNode(const string path,map<int,vector<string> > &m_node_map_all);

private:
	static void NewInstance();
	static ZkWrapper& GetClient();

  string fullPath(const string node);

  void ExpiredHandlerImp();
	static void DefaultSessionExpiredHandlerImp(void* context);
	static void SessionWatcher(zhandle_t* zh, int type, int state, const char* path, void* watcher_ctx);
	static void* SessionCheckThreadMain(void* arg);

	// GetNode的zk回调处理
	static void GetNodeDataCompletion(int rc, const char* value, int value_len,const struct Stat* stat, const void* data);
	static void GetNodeWatcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx);

	// GetChildren的zk回调处理
	static void GetChildrenStringCompletion(int rc, const struct String_vector* strings, const void* data);
	static void GetChildrenWatcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx);
	
	void ChildrenListen(const string path, bool* changeTag,ChildrenListenHandler handler);
	static void ChildrenListen_Completion(int rc, const struct String_vector* strings, const void* data);
	static void ChildrenListen_Watcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx);
	
	void ChildrenListen2(const string path,
	                     set<string>* fileTypeListForTask,
		                   set<string>* pathListForListen,
		                   map<string,bool>* watcherMap,
											 ChildrenListenHandler2 handler);
	static void ChildrenListen2_Completion(int rc, const struct String_vector* strings, const void* data);
	static void ChildrenListen2_Watcher(zhandle_t* zh, int type, int state, const char* path,void* watcher_ctx);

	// Exist的zk回调处理
	static void ExistCompletion(int rc, const struct Stat* stat, const void* data);
	static void ExistWatcher(zhandle_t* zh, int type, int state, const char* path, void* watcher_ctx);
	
	void NodeListen_Exist(const string path,map<string,bool>* watcherMap, const string fileType, NodeListenHandler handler);
	static void NodeListen_ExistCompletion(int rc, const struct Stat* stat, const void* data);
	static void NodeListen_ExistWatcher(zhandle_t* zh, int type, int state, const char* path, void* watcher_ctx);

	// Create的zk回调处理
	static void CreateCompletion(int rc, const char* value, const void* data);

	// Set的zk回调处理
	static void SetCompletion(int rc, const struct Stat* stat, const void* data);

	// Delete的zk回调处理
	static void DeleteCompletion(int rc, const void* data);

	void UpdateSessionState(zhandle_t* zhandle, int state);
	void CheckSessionState();
	
	void THROW_EXCEPTION(const int linenum ,const int rc);

	int64_t GetCurrentMs();

private:
	
	static pthread_once_t new_instance_once_;
	
	int  redoTimes;//重试次数
	long sleepTime;//重试间隔时间(秒)
	
	string root;
	string constr;

	zhandle_t* zhandle_; //zk 连接对象
	FILE* log_fp_;       //zk 日志
	SessionExpiredHandler expired_handler_;//session失效,回调函数
	void* user_context_;//expired_handler_ 依赖内容
	
  SessionExpiredHandler reconnect_handler_;//session闪断,回调函数
	void* reconnect_context_;//reconnect_handler_ 依赖内容

	int session_state_;           // ZK会话状态
	int session_timeout_;         // zk连接超时设置
	int64_t session_disconnect_ms_;//连接异常开始时间
	pthread_mutex_t state_mutex_; //状态锁
	pthread_cond_t  state_cond_;  //状态条件

	// ZK会话状态检测线程（由于zk精确到毫秒，所以毫秒级间隔check）
	bool session_check_running_;  //是否运行状态
	pthread_t session_check_tid_; //检查线程
	
	string waitLockNode;         // 等待释放的节点
	string currentLockNode;      // 当前锁定的节点
	
	bool zk_active; //链接是否可用
};


#endif /* ZK_WRAPPER_H_ */
