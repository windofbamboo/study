#ifndef RESINFO_ZKCLIENT_H_
#define RESINFO_ZKCLIENT_H_

#include <vector>
#include <set>
#include <algorithm>
#include <time.h> 
#include <sys/timeb.h>
#include "ResZkPathOperator.h"

using namespace std;
using namespace ZK;

void sessionHandlerImp(void* context);

void nodeListenHandlerImp(ZKErrorCode errcode, void* context);

void childrenListenHandlerImp(ZKErrorCode errcode, const string path,bool* changeTag);

void childrenListenHandler2Imp(ZKErrorCode errcode, int count, char** data, void* context);

class ResInfoZkClient: public ResZkPathOperator
{
private:	
	
	string zkConnectList;
	ZkActiveValue zkActiveValueOwn;
	
	map<string, map<string, ZkOutput> > assignInputMap;
	int suffixModuleInstanceNumber;
	bool isSuffixModuleInstanceNumberWatcherAdded;
	bool isRegisterOk;

	map<string,bool> assignInputListWatcherOnOff;

	bool suffixModuleInstanceNumberWatcherOnOff;

	set<string> fileTypeListForTask;
	set<string> pathListForListen;
		
	pthread_mutex_t task_mutex_;
	
public:
	
	ResInfoZkClient();
	
	ResInfoZkClient(const string zkConnectList, 
									const string zkRoot, 
									const string moduleName, 
									const string channelId,
									const int redoTimes = -1,
									const int sleepTime = -1,
									SessionExpiredHandler handlerImp = NULL,
									void* context= NULL);
	
	~ResInfoZkClient();
	
	void login();
	
	void unLoginr();
	
	void registerOutInfo(map<string,ZkFileTypeValue> zkFileTypeValues,bool isForceCurHost);
	
	map<string, map<string, ZkOutput> > refreshAllTaskListBySuffixModule();
	
	int getSuffixModuleInstanceNumberByPrefixModule(const string suffixModuleName);
	
private:
	void doTempNodeForConcurrence(const string instanceActivePath);	
	
	void insertTempNode(const string instanceActivePath,const string value);
	
	void insertAssignInputMap(string fileType, 
														vector<ZkOutput> &inputVec);
};

#endif /* RESINFO_ZKCLIENT_H_ */