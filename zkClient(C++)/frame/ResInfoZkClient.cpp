#include "ResInfoZkClient.h"

void nodeListenHandlerImp (ZKErrorCode errcode, void* context)
{
	NodeWatchContext*  watch_ctx 	 = (NodeWatchContext*)context;
	string fileType                = watch_ctx->fileType;
	string path                    = watch_ctx->path;
	map<string, bool>* watcherMap  = watch_ctx->watcherMap;
	
	if (errcode == kZKSucceed) {

		bool insertTag = false;
		map<string, bool>::iterator itr = watcherMap->find(fileType);
		if(itr!=watcherMap->end()){
			 if(!(itr->second))
			 	insertTag=true;
		}else{
			insertTag=true;
		}
		
		if(insertTag)
			watcherMap->insert(map<string, bool>::value_type(fileType, true));
		
		#ifdef _DEBUG_
		  cout<<"nodeListenHandlerImp node:"<<path<<" kZKSucceed !"<<endl;
		#endif
		
	} else if (errcode == kZKNotExist) {
		#ifdef _DEBUG_
		  cout<<"nodeListenHandlerImp node:"<<path<<" ,errCode :kZKNotExist !"<<endl;
		#endif
	} else if (errcode == kZKDeleted) {
		#ifdef _DEBUG_
		  cout<<"nodeListenHandlerImp node:"<<path<<" ,errCode :kZKDeleted !"<<endl;
		#endif
	} else if (errcode == kZKError) {
		#ifdef _DEBUG_
		  cout<<"nodeListenHandlerImp node:"<<path<<" ,err!"<<endl;
		#endif
	}
}

void childrenListenHandlerImp(ZKErrorCode errcode, const string path, bool* changeTag) {
	if (errcode == kZKSucceed) {
		(*changeTag) = true;
		#ifdef _DEBUG_
		  cout<<"childrenListenHandlerImp node:"<<path<<" kZKSucceed !"<<endl;
		#endif
	} else if (errcode == kZKDeleted) {
		#ifdef _DEBUG_
		  cout<<"childrenListenHandlerImp node:"<<path<<" ,errCode :kZKDeleted !"<<endl;
		#endif
	} else if (errcode == kZKNotExist ) {
		#ifdef _DEBUG_
		  cout<<"childrenListenHandlerImp node:"<<path<<" ,errCode :kZKNotExist !"<<endl;
		#endif
	} else if (errcode == kZKError) {
		#ifdef _DEBUG_
		  cout<<"childrenListenHandlerImp node:"<<path<<" ,err !"<<endl;
		#endif
	}
}

void childrenListenHandler2Imp(ZKErrorCode errcode,int count, char** data, void* context) {
	
	ChildWatchContext2* watch_ctx 	= (ChildWatchContext2*)context;
	string path = watch_ctx->path;
	
	if (errcode == kZKSucceed) {
		
		#ifdef _DEBUG_
		  cout<<"childrenListenHandler2Imp start!"<<endl;
		#endif
		set<string>* fileTypeListForTask= watch_ctx->fileTypeListForTask;
		set<string>* pathListForListen 	= watch_ctx->pathListForListen;
		map<string,bool>* watcherMap		=	watch_ctx->watcherMap;
		
		vector<string> fileTypeVec;
		for(int i=0;i<count;i++){
			string t_node = data[i];
      fileTypeVec.push_back(t_node);
		}
		
		fileTypeListForTask->clear();
		for(vector<string>::iterator itr =fileTypeVec.begin();
																 itr!=fileTypeVec.end();itr++)
		{
			fileTypeListForTask->insert(*itr);
		}
		
		//增加节点监听
		for(set<string>::iterator itr =fileTypeListForTask->begin();
			                        itr!=fileTypeListForTask->end();itr++)
		{	
			set<string>::iterator itr2 = std::lower_bound(pathListForListen->begin(),pathListForListen->end(),*itr);
			
			if(itr2!=pathListForListen->end()){
				continue;
			}
			pathListForListen->insert(*itr);	
			
			string subPath=path+ZK_PATH_SEP+*itr+ZK_PATH_SEP+ZK_ASSIGN_INPUT_LIST;
			
			watch_ctx->zkWrapper->setListenterNode(subPath,watcherMap,*itr,nodeListenHandlerImp);
		}
		
		//删除多余节点的监听(不必要)
		for(set<string>::iterator itr=pathListForListen->begin();
			                       itr!=pathListForListen->end();itr++)
		{
			set<string>::iterator itr2 =
				std::lower_bound(fileTypeListForTask->begin(),fileTypeListForTask->end(),*itr);
			if(itr2==fileTypeListForTask->end()){
				watch_ctx->zkWrapper->deleteListenterNode(*itr);
			}
		}
		
		#ifdef _DEBUG_
		  cout<<"childrenListenHandler2Imp end!"<<endl;
		#endif
		
	} else if (errcode == kZKDeleted) {
		#ifdef _DEBUG_
		  cout<<"childrenListen2 node:"<<path<<" ,errCode :kZKDeleted !"<<endl;
		#endif
	} else if (errcode == kZKNotExist) {
		#ifdef _DEBUG_
		  cout<<"childrenListen2 node:"<<path<<" ,errCode :kZKNotExist !"<<endl;
		#endif
	} else if (errcode == kZKError) {
		#ifdef _DEBUG_
		  cout<<"childrenListen2 node:"<<path<<" ,err !"<<endl;
		#endif
	}
}

void sessionHandlerImp(void* context){
	
	#ifdef _DEBUG_
	  cout<<"sessionHandlerImp start!"<<endl;
	#endif
	
	SessionContext* data = (SessionContext*) context;
	
	string path          = data->path;
	string value         = data->value;
	string json          = data->json;
	ZkWrapper* zkWrapper = data->zkWrapper;
	
	#ifdef _DEBUG_
	  cout<<"sessionHandlerImp,path = "<<path<<",value = "<<value<<",json ="<<json<<endl;
	#endif
	
	string nodeValue;
	for(;;)
	{
		try {
			#ifdef _DEBUG_
			  cout<<"insertTempNode path:"<<path<<" value: "<<value<<endl;
			#endif
			zkWrapper->insertTempNode(path,value);
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
			  cout<<"insert node:"<<data->path<<" err "
			      <<__FILE__<<__LINE__<<endl;
			#endif
		}
		
		try {
			nodeValue= zkWrapper->getNodeValue(data->path);
			break;
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
			  cout<<"getNodeValue node:"<<data->path<<" err "
			      <<__FILE__<<__LINE__<<endl;
			#endif
		}
	}
	
	ZkActiveValue zkActiveValueOld;
	ZkActiveValue zkActiveValueNew;
	
	if(!Json2ZkActiveValue(json,zkActiveValueOld)){
		#ifdef _DEBUG_
			cout<<"Json2ZkActiveValue err! "<<__FILE__<<__LINE__<<endl;
		#endif
	}
	
	if(!Json2ZkActiveValue(nodeValue,zkActiveValueNew)){
		#ifdef _DEBUG_
			cout<<"Json2ZkActiveValue err! "<<__FILE__<<__LINE__<<endl;
		#endif
	}
	
	if(!(zkActiveValueOld == zkActiveValueNew) ){
		#ifdef _DEBUG_
			  cout<<"CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath="
			      <<data->path<<",new="<<zkActiveValueNew<<",old="<<zkActiveValueOld<<endl;
		#endif
		abort();
	}else{
		#ifdef _DEBUG_
			  cout<<"CHANNEL INSTANCE BE RE-TAKED BY OWN PROCCESS!instanceActivePath="
			      <<data->path<<",new="<<zkActiveValueNew<<",old="<<zkActiveValueOld<<endl;
		#endif
	}
	
	delete data;
	#ifdef _DEBUG_
	  cout<<"sessionHandlerImp end!"<<endl;
	#endif
}

ResInfoZkClient::ResInfoZkClient()
{
	assignInputMap.clear();
	suffixModuleInstanceNumber = -1;
	isSuffixModuleInstanceNumberWatcherAdded=false;
	isRegisterOk = false;
	assignInputListWatcherOnOff.clear();
	suffixModuleInstanceNumberWatcherOnOff = false;
	
	pthread_mutex_init(&task_mutex_, NULL);
}

ResInfoZkClient::~ResInfoZkClient()
{
	assignInputMap.clear();
	assignInputListWatcherOnOff.clear();
	
	pthread_mutex_destroy(&task_mutex_);
}

ResInfoZkClient::ResInfoZkClient(	const string zkConnectList, 
																	const string zkRoot, 
																	const string moduleName, 
																	const string channelId,
																	const int redoTimes,
                                  const int sleepTime,
																	SessionExpiredHandler handlerImp,
																	void* context)
{
	this->zkConnectList = zkConnectList;
	this->moduleName 		= moduleName;
	this->channelId 		= channelId;
	this->zkRoot        = zkRoot;
	this->zkActiveValueOwn = ZkActiveValue(channelId);
	this->isRegisterOk	= false;
	this->suffixModuleInstanceNumber 							 = -1;
	this->isSuffixModuleInstanceNumberWatcherAdded = false;
	this->suffixModuleInstanceNumberWatcherOnOff   = false;
	
	pthread_mutex_init(&task_mutex_, NULL);
	
	this->zkWrapper = ZkWrapper::GetInstance();
	if(this->zkWrapper.Init(zkConnectList, connectionTimeoutMs,zkRoot,redoTimes,sleepTime,handlerImp,context)){
		#ifdef _DEBUG_
      cout<<"ResInfoZkClient zkWrapper Init sucess!"<<endl;
		#endif
	}else{
		#ifdef _DEBUG_
      cout<<"ResInfoZkClient zkWrapper Init fail!"<<endl;
		#endif
	}
}

void ResInfoZkClient::login()
{
	if (isRegisterOk)
			return;
			
		string instanceRootPath  = getModuleInstanceRootPath(zkRoot,moduleName,channelId,ROOT_TYPE_REGISTER);
		string instanceActivePath= instanceRootPath+ZK_PATH_SEP+ZK_ISACTIVE;
		
		//注册根目录
		try{
			zkWrapper.insertNode(instanceRootPath);
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
			   cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
			#endif
		}
		// 处理临时节点，必须保证是当前进程所属的临时节点
		doTempNodeForConcurrence(instanceActivePath);
		// 增加连接监听
		
		string value;
		try{
		  value = zkWrapper.getNodeValue(instanceActivePath);
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
			   cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
			#endif
		}

		ZkActiveValue zkActiveValue;
		if(!Json2ZkActiveValue(value,zkActiveValue)){
			#ifdef _DEBUG_
			   cout<<"parse ZkActiveValue err !"<<endl;
			#endif
		}
		
		ZkActiveValue zkActiveValue2 = ZkActiveValue(channelId);
		string json2 = ZkActiveValue2Json(zkActiveValue2);
		
		SessionContext context = SessionContext(instanceActivePath,json2,value,&zkWrapper);
		zkWrapper.setListenterConnect(&context,sessionHandlerImp);//闪断回调函数

		isRegisterOk = true;
		#ifdef _DEBUG_
			cout<<"=============================REGISTER OK!============================="<<endl;
		#endif
}

void ResInfoZkClient::unLoginr()
{
	if (!isRegisterOk) {// 异常或注册失败
		return;
	}
	string instanceRootPath  = getModuleInstanceRootPath(zkRoot, moduleName, channelId,ROOT_TYPE_REGISTER);
	string instanceActivePath= instanceRootPath+ZK_PATH_SEP+ZK_ISACTIVE;
	
	string value; 
	try{
		value = zkWrapper.getNodeValue(instanceActivePath);
	}catch(ZK_Exception e){
		#ifdef _DEBUG_
		   cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
	
	ZkActiveValue zkActiveValue;
	if(Json2ZkActiveValue(value,zkActiveValue)){
		if(zkActiveValue == ZkActiveValue(channelId))
			try{
				#ifdef _DEBUG_
					cout<<"zkWrapper.deleteTempNode("<<instanceActivePath<<")... "<<endl;
				#endif
				zkWrapper.deleteTempNode(instanceActivePath);
			}catch(ZK_Exception e){
				#ifdef _DEBUG_
			   cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
				#endif
			}
	}
	
	isRegisterOk = false;
}

void ResInfoZkClient::registerOutInfo(map<string, ZkFileTypeValue> zkFileTypeValues,bool isForceCurHost)
{
	char hostname[32];
	string host;
	if(gethostname(hostname,sizeof(hostname)) )
	{
	  host = "localhost";
	  #ifdef _DEBUG_
      cout<<"gethostname err!"<<__FILE__<<__LINE__<<endl;
    #endif
	}else{
		host=hostname;
	}
		
	string fileType;
	vector<string> fileTypeOlds = getAllFileTypes(zkRoot, moduleName, channelId);
	
	map<string, ZkFileTypeValue>::iterator itr;
	for(itr=zkFileTypeValues.begin();itr!=zkFileTypeValues.end();itr++)
	{
		fileType = itr->second.getFileType();
		vector<ZkOutput> zkOutputNews = itr->second.getZkOutputs();
		vector<ZkOutput> zkErrOutNews = itr->second.getZkErrOuts();
		vector<ZkOutput> zkOutputOlds = getOutputByFileType(zkRoot, moduleName, channelId, fileType);
		vector<ZkOutput> zkErrOutOlds = getErrOutByFileType(zkRoot, moduleName, channelId, fileType);
		
		if(isForceCurHost){
			for(vector<ZkOutput>::iterator iter=zkOutputNews.begin();
																		 iter!=zkOutputNews.end();++iter)
			{
				iter->setHostOrIp(host);
			}
			for(vector<ZkOutput>::iterator iter=zkErrOutNews.begin();
																		 iter!=zkErrOutNews.end();++iter)
			{
				iter->setHostOrIp(host);
			}
		}
		
		// 先处理output
		if (!zkOutputNews.empty()) {
			if (!zkOutputOlds.empty()) {
				modifyOutputByFileType(zkRoot, moduleName, channelId, fileType, zkOutputOlds, zkOutputNews);
			} else {
				addOutputByFileType(zkRoot, moduleName, channelId, fileType, zkOutputNews);
			}
		}
		// 处理errput
		if (!zkErrOutNews.empty()) {
			if (!zkErrOutOlds.empty()) {
				modifyErrOutByFileType(zkRoot, moduleName, channelId, fileType, zkErrOutOlds, zkErrOutNews);
			} else {
				addErrOutByFileType(zkRoot, moduleName, channelId, fileType, zkErrOutNews);
			}
		}
		
		if (zkOutputNews.empty() && zkErrOutNews.empty()) {
			deleteByFileType(zkRoot, moduleName, channelId, fileType);
		}
	}
	// 删除不需要的文件类型
	vector<string>::iterator iter;
	for(iter=fileTypeOlds.begin();iter!=fileTypeOlds.end();iter++)	
	{
		itr = zkFileTypeValues.find(*iter);
		if(itr!=zkFileTypeValues.end()){
			deleteByFileType(zkRoot, moduleName, channelId, *iter);
		}
	}
}

map<string, map<string, ZkOutput> > ResInfoZkClient::refreshAllTaskListBySuffixModule()
{
	if (!isRegisterOk) {
		return assignInputMap;
	}

	string instanceRootPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId,ROOT_TYPE_REGISTER);
	string inputListStr;
	vector<ZkOutput> inputList;
	map<string, ZkOutput> inputmapByFileType;

	if (assignInputMap.empty()) {// 仅运行一次
		// 注册输入信息内容同步的监听器
		vector<string> fileTypes = getAllFileTypes(zkRoot, moduleName, channelId);
		pthread_mutex_lock(&task_mutex_);	
		for(vector<string>::iterator itr=fileTypes.begin();itr!=fileTypes.end();itr++)
		{
			fileTypeListForTask.insert(*itr);
			
			set<string>::iterator itlow = std::lower_bound(pathListForListen.begin(),pathListForListen.end(),*itr);
			if(itlow!=pathListForListen.end()) {
				continue;//已经增加过监听了
			}
			pathListForListen.insert(*itr);
			//设置监听
			string subPath = instanceRootPath+ZK_PATH_SEP+*itr+ZK_PATH_SEP+ZK_ASSIGN_INPUT_LIST;
			try{
				zkWrapper.setListenterNode(subPath,&assignInputListWatcherOnOff,*itr,nodeListenHandlerImp);
				#ifdef _DEBUG_
		      cout<<"setListenterNode - node :"<<subPath<<endl;
		    #endif
			}catch(ZK_Exception e){
				#ifdef _DEBUG_
		      cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		    #endif
			}
		}
		// 注册输入信息内容的父节点（即文件类型）的变化的监听器
		try{
			zkWrapper.setListenterChild(instanceRootPath,&fileTypeListForTask,&pathListForListen,&assignInputListWatcherOnOff,childrenListenHandler2Imp);
			#ifdef _DEBUG_
		     cout<<"setListenterChild - node :"<<instanceRootPath<<endl;
		  #endif
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
		    cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		  #endif
		}

		assignInputMap.clear();
		for(set<string>::iterator itr =fileTypeListForTask.begin();
			                        itr!=fileTypeListForTask.end();itr++)
		{
			string subPath = instanceRootPath+ZK_PATH_SEP+*itr+ZK_PATH_SEP+ZK_ASSIGN_INPUT_LIST;
			#ifdef _DEBUG_
		    	cout<<"subPath :"<<subPath<<endl;
		  #endif
			try {
				inputListStr = zkWrapper.getNodeValue(subPath);
			} catch (ZK_Exception& e) {
				#ifdef _DEBUG_
		    	cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		  	#endif
		  	inputListStr = "";
				continue;
			}
			
			vector<ZkOutput> inputList;
			if(!Json2ZkOutputList(inputListStr,inputList))
				continue;
			if(inputList.empty())
				continue;
				
			insertAssignInputMap(*itr,inputList);
		}
		
		pthread_mutex_unlock(&task_mutex_);
	} 
	else {// 处理监听事件
		//获取监听事件
		map<string,bool> assignInputListWatcherOnOffClone;
		assignInputListWatcherOnOffClone.swap(assignInputListWatcherOnOff);
		string fileType;
		//处理监听事件
		for(map<string,bool>::iterator itr =assignInputListWatcherOnOffClone.begin();
																	 itr!=assignInputListWatcherOnOffClone.end();itr++) 
		{
			fileType = itr->first;
			map<string,map<string,ZkOutput> >::iterator itr_assign;
			itr_assign = assignInputMap.find(fileType);
			if(itr_assign!=assignInputMap.end()){
				itr_assign->second.clear();
			}
			
			string subPath=instanceRootPath+ZK_PATH_SEP+fileType+ZK_PATH_SEP+ZK_ASSIGN_INPUT_LIST;
			try {
				inputListStr = zkWrapper.getNodeValue(subPath);
			} catch (ZK_Exception& e) {
				#ifdef _DEBUG_
		    	cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		  	#endif
				inputListStr[0] = '\0';
				continue;
			}
			vector<ZkOutput> inputList;
			if(Json2ZkOutputList(inputListStr,inputList))
				insertAssignInputMap(fileType,inputList);
		}
	}
	return assignInputMap;
}

int ResInfoZkClient::getSuffixModuleInstanceNumberByPrefixModule(const string suffixModuleName)
{
	if (!isRegisterOk) {
		return suffixModuleInstanceNumber;
	}
	if (!isSuffixModuleInstanceNumberWatcherAdded) {// 仅运行一次
		// 注册输入信息同步的监听器
		string moduleRootPath = getModuleRootPath(zkRoot, suffixModuleName, ROOT_TYPE_REGISTER);
		try{
			#ifdef _DEBUG_
		  	cout<<"setListenterChild - node:"<<moduleRootPath<<endl;
		  #endif
			zkWrapper.setListenterChild(moduleRootPath,&suffixModuleInstanceNumberWatcherOnOff,childrenListenHandlerImp);
		} catch (ZK_Exception& e) {// 别的进程已经退出
			#ifdef _DEBUG_
		  	cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		  #endif
		}
		
		isSuffixModuleInstanceNumberWatcherAdded=true;
		suffixModuleInstanceNumber = getModuleInstanceNumber(zkRoot, suffixModuleName);
		return suffixModuleInstanceNumber;
	} else {
		if (suffixModuleInstanceNumberWatcherOnOff) {
			suffixModuleInstanceNumberWatcherOnOff = false;// 重置监听
			suffixModuleInstanceNumber = getModuleInstanceNumber(zkRoot, suffixModuleName);
			return suffixModuleInstanceNumber;
		}
	}
	return suffixModuleInstanceNumber;
}

void ResInfoZkClient::doTempNodeForConcurrence(const string instanceActivePath)
{
	bool isExist = false;
	
	try {
		isExist = zkWrapper.isTempNodeExist(instanceActivePath);
	}catch (ZK_Exception& e) {
		#ifdef _DEBUG_
			cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
	
	if (isExist) {// 如果临时节点已经存在，需检查是否是自己创建的
		// 如果是其他进程，需等待进程退出后再判断
		ZkActiveValue zkActiveValue;
		long long time_last;
		time_last = time(NULL);
		
		for (;;) {
			string value;
			try {
				value = zkWrapper.getNodeValue(instanceActivePath);
			} catch (ZK_Exception& e) {// 别的进程已经退出
				#ifdef _DEBUG_
		    	cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		  	#endif
				break;
			}
			if(!Json2ZkActiveValue(value, zkActiveValue)){
				break;
			}
			if(!(zkActiveValue==zkActiveValueOwn)){
				break;
			}
			
			long long time_new;
			time_new = time(NULL);
			
			if (time_new - time_last >= 180) {// 说明其他的进程一直存活
				#ifdef _DEBUG_
		    	cout<<"CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath="
		    	    <<instanceActivePath<<",zk=" <<zkActiveValue<<",own="<<zkActiveValueOwn
		    	    <<",It maybe active,Now Current proccess will exit now!"<<endl;	
		  	#endif
				
				abort();
			} else {
				#ifdef _DEBUG_
		    	cout<<"CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath="
		    	    <<instanceActivePath<<",zk=" <<zkActiveValue<<",own="<<zkActiveValueOwn
		    	    <<",Watching: Is it active..."<<endl;
		  	#endif
				
				sleep(3000);
			}
			
			value= ZkActiveValue2Json(zkActiveValueOwn);
			insertTempNode(instanceActivePath,value);
		}
	}
	// 不存在就更新临时节点
	string value= ZkActiveValue2Json(zkActiveValueOwn);
	insertTempNode(instanceActivePath,value);
}

void ResInfoZkClient::insertTempNode(const string instanceActivePath,const string value)
{
	try {
		zkWrapper.insertTempNode(instanceActivePath, value);
	}catch (ZK_Exception& e) {
		#ifdef _DEBUG_
		   cout<<"PROCCESS ALREADY EXISTS!instanceActivePath="
		       <<instanceActivePath<<",own="<<zkActiveValueOwn
		       <<", errCode :"<<e.getErrCode()
		       <<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
	// 再次核对是否自己进程注册
	string result;
	try {
		result= zkWrapper.getNodeValue(instanceActivePath);
	}catch (ZK_Exception& e) {
		#ifdef _DEBUG_
		   cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
	ZkActiveValue zkActiveValue;
	
	if(!Json2ZkActiveValue(result,zkActiveValue)){
		abort();
	}
	
	if (zkActiveValue ==zkActiveValueOwn) {
		#ifdef _DEBUG_
		   cout<<"CHANNEL INSTANCE BE RE-TAKED BY OWN PROCCESS!instanceActivePath="<<instanceActivePath
		       <<",zk="<<zkActiveValue<<",own="<<zkActiveValueOwn<<endl;
		#endif
	} else {
		#ifdef _DEBUG_
		   cout<<"CHANNEL INSTANCE BE ALREADY TAKED BY OWN PROCCESS!instanceActivePath="<<instanceActivePath
		       <<",zk="<<zkActiveValue<<",own="<<zkActiveValueOwn<<endl;
		#endif
		abort();
	}
}


void ResInfoZkClient::insertAssignInputMap(string fileType, vector<ZkOutput> &inputVec)
{
	if(inputVec.empty())
		return;
	
	typedef map<string, ZkOutput>::value_type valType1;
	typedef map<string,map<string, ZkOutput> >::value_type valType2;
	
	map<string,ZkOutput> inputmapByFileType;
	map<string,map<string,ZkOutput> >::iterator itr_assign;
	itr_assign = assignInputMap.find(fileType);
	if(itr_assign!=assignInputMap.end()){
		 assignInputMap.erase(itr_assign);
	}
	
	for(vector<ZkOutput>::iterator itr=inputVec.begin();itr!=inputVec.end();itr++)
	{
		ZkOutput zkOutput = *itr;
		zkOutput.instancePath();
		inputmapByFileType.insert(valType1(itr->getDirId(),zkOutput));
	}
	if(!inputmapByFileType.empty()){
		assignInputMap.insert(valType2(fileType,inputmapByFileType));
	}
}

