#include <stdio.h>
#include <unistd.h>
#include <string>
#include "ResInfoZkClient.h"

using namespace std;
using namespace ZK;

struct ProcContext {
	ProcContext(const string path,const string value){		
		this->path  = path;
		this->value = value;
	};

	string path;
	string value;
}; 


void handlerImp(void* context){
	
	ProcContext* data = (ProcContext*)context;
	delete data;
	printf(" session end!\n");
}

//session闪断
void handlerImp2(void* context){
	
	cout<<"handlerImp2 execute !"<<endl;
}

int testExpiredHandler() {
	
	cout<<"testExpiredHandler  start !"<<endl;
	
	string zkConnectList = "10.21.20.38:32000,10.21.20.38:32001,10.21.20.38:32002";
	string zkRoot        = "/roam-gsm";
	string channelId     = "101";
	int  redoTimes = 3;
	long sleepTime = 5;
	
	ProcContext* context = new ProcContext(zkRoot,channelId);

	ZkWrapper& zkWrapper = ZkWrapper::GetInstance();
	zkWrapper.Init(zkConnectList, connectionTimeoutMs,zkRoot,redoTimes,sleepTime,handlerImp,context);

	zkWrapper.setListenterConnect(context,handlerImp2);
	
	do {
		sleep(600);
	} while(false);
	
	cout<<"testExpiredHandler  end !"<<endl;
	
	return 0;
}


int testClient() {
	
	cout<<"testClient  start !"<<endl;
	
	string ZkConnectList = "10.21.20.38:32000,10.21.20.38:32001,10.21.20.38:32002";
	string zkRoot        = "/roam-gsm";
	string channelId     = "101";
	int  redoTimes = 3;
	long sleepTime = 5;
	
	ProcContext* context = new ProcContext(zkRoot,channelId);
	
	ResInfoZkClient resInfoZkClient = ResInfoZkClient(ZkConnectList,zkRoot,MODULE_NAME_ROAMDEAL,channelId,
																										redoTimes,sleepTime,handlerImp,context);
	resInfoZkClient.login();
	ZkWrapper zkWrapper = resInfoZkClient.getZkWrapper();
	
	string fileType        = "gsm-type";
	string anotherFileType = "test-type";
	map<string,ZkFileTypeValue> zkFileTypeValues;
	ZkFileTypeValue             zkFileTypeValue;
	ZkOutput                    zkOutput;
	vector<ZkOutput>            zkOutputVec;
	
	zkOutput.setConcWorkPath("a");
	zkOutput.setDirId				("gsm-test01");
	zkOutput.setFileType		(fileType);
	zkOutput.setOutSplitNum	(3);
	zkOutput.setPath				("/a/b/c");

	zkOutputVec.push_back(zkOutput);
	zkFileTypeValue.setFileType(fileType);
	zkFileTypeValue.setZkOutputs(zkOutputVec);
	zkFileTypeValues.insert(map<string, ZkFileTypeValue>::value_type(fileType, zkFileTypeValue));

	resInfoZkClient.registerOutInfo(zkFileTypeValues,true);
	
	//-----------------------------------
	
	zkOutputVec.clear();
	zkOutputVec.push_back(zkOutput);

	zkOutput.setConcWorkPath("d");
	zkOutput.setDirId				("gsm-test02");
	zkOutput.setFileType		(fileType);
	zkOutput.setOutSplitNum	(0);
	zkOutput.setPath				("/a/b/d");
	
	zkOutputVec.push_back(zkOutput);
	
	string path = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+MODULE_NAME_ROAMDEAL+ZK_PATH_SEP
	              +"101"+ZK_PATH_SEP+fileType+ZK_PATH_SEP+ZK_ASSIGN_INPUT_LIST;
	string value = ZkOutputList2Json(zkOutputVec);
	
	cout<<"insertNode  path :"<<path<<" value :"<<value<<endl;
	zkWrapper.insertNode(path,value);
	
	zkOutputVec.clear();
	
	zkOutput.setConcWorkPath("e");
	zkOutput.setDirId				("test01");
	zkOutput.setFileType		(anotherFileType);
	zkOutput.setOutSplitNum	(5);
	zkOutput.setPath				("/a/b/e");
	zkOutputVec.push_back(zkOutput);
	
	path = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+MODULE_NAME_ROAMDEAL+ZK_PATH_SEP
				 +"101"+ZK_PATH_SEP+anotherFileType+ZK_PATH_SEP+ZK_ASSIGN_INPUT_LIST;
	value = ZkOutputList2Json(zkOutputVec);
	
	cout<<"insertNode  path :"<<path<<" value :"<<value<<endl;
	zkWrapper.insertNode(path,value);
	
	{
		map<string, map<string, ZkOutput> > taskList = resInfoZkClient.refreshAllTaskListBySuffixModule();
		cout<<"taskList  size :"<<taskList.size()<<endl;
		map<string, map<string, ZkOutput> >::iterator itr = taskList.find(fileType);
		if(itr!=taskList.end()){
			itr->second.size();
			cout<<"fileType  size :"<<itr->second.size()<<endl;
		}
		itr = taskList.find(anotherFileType);
		if(itr!=taskList.end()){
			itr->second.size();
			cout<<"anotherFileType  size :"<<itr->second.size()<<endl;
		}
	}
	
	//新增一个目录
	zkOutput.setConcWorkPath("f");
	zkOutput.setDirId				("test02");
	zkOutput.setFileType		(anotherFileType);
	zkOutput.setOutSplitNum	(10);
	zkOutput.setPath				("/a/b/f");
	zkOutputVec.push_back(zkOutput);
	
	path = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+
				 MODULE_NAME_ROAMDEAL+ZK_PATH_SEP+"101"+ZK_PATH_SEP+anotherFileType+ZK_PATH_SEP+ZK_ASSIGN_INPUT_LIST;
	value = ZkOutputList2Json(zkOutputVec);
	cout<<"insertNode  path :"<<path<<" value :"<<value<<endl;
	zkWrapper.insertNode(path,value);

	sleep(3);//因为是多线程操作，所以需要等待一会儿
	
	{
		map<string, map<string, ZkOutput> > taskList = resInfoZkClient.refreshAllTaskListBySuffixModule();
		cout<<"taskList  size :"<<taskList.size()<<endl;
		map<string, map<string, ZkOutput> >::iterator itr = taskList.find(fileType);
		if(itr!=taskList.end()){
			itr->second.size();
			cout<<"fileType  size :"<<itr->second.size()<<endl;
		}
		itr = taskList.find(anotherFileType);
		if(itr!=taskList.end()){
			itr->second.size();
			cout<<"anotherFileType  size :"<<itr->second.size()<<endl;
		}
	}

	resInfoZkClient.unLoginr();
	cout<<"testClient  end !"<<endl;
	
	return 0;
}


int testClientSuffixModuleInstanceNumber() {
	
	cout<<"testClientSuffixModuleInstanceNumber  start !"<<endl;
	
	string ZkConnectList = "10.21.20.38:32000,10.21.20.38:32001,10.21.20.38:32002";
	string zkRoot        = "/roam-gsm";
	string channelId     = "101";
	int  redoTimes = 3;
	long sleepTime = 5;
	
	ResInfoZkClient resInfoZkClient = ResInfoZkClient(ZkConnectList, zkRoot,MODULE_NAME_ROAMDEAL,
	                                                  channelId,redoTimes,sleepTime,handlerImp);
	resInfoZkClient.login();
	ZkWrapper zkWrapper = resInfoZkClient.getZkWrapper();
	
	string fileType = "gsm-type";
	map<string, ZkFileTypeValue> zkFileTypeValues;
	ZkFileTypeValue              zkFileTypeValue;
	ZkOutput                     zkOutput;
	vector<ZkOutput>             zkOutputVec;
	
	zkOutput.setConcWorkPath("a");
	zkOutput.setDirId				("gsm-test01");
	zkOutput.setFileType		(fileType);
	zkOutput.setOutSplitNum	(3);
	zkOutput.setPath				("/a/b/c");
	
	zkOutputVec.push_back(zkOutput);
	zkFileTypeValue.setFileType(fileType);
	zkFileTypeValue.setZkOutputs(zkOutputVec);
	zkFileTypeValue.setZkErrOuts(zkOutputVec);
	
	zkFileTypeValues.insert(map<string,ZkFileTypeValue>::value_type(fileType, zkFileTypeValue));
	
	resInfoZkClient.registerOutInfo(zkFileTypeValues,true);

	//-----------------------------------
	string path = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+MODULE_NAME_RR+ZK_PATH_SEP+"100"+ZK_PATH_SEP+ZK_ISACTIVE;
	ZkActiveValue zkActiveValue1 = ZkActiveValue("100");
	string value = ZkActiveValue2Json(zkActiveValue1);
	
	cout<<"insertNode  path :"<<path<<" value :"<<value<<endl;
	zkWrapper.insertNode(path,value);

	path = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+MODULE_NAME_RR+ZK_PATH_SEP+"101";
	cout<<"deleteNode path :"<<path<<endl;
	zkWrapper.deleteNode(path);

	int suffixModuleInstanceNumber = resInfoZkClient.getSuffixModuleInstanceNumberByPrefixModule(MODULE_NAME_RR);
	cout<<"suffixModuleInstanceNumber  :"<<suffixModuleInstanceNumber<<endl;
	//assert(suffixModuleInstanceNumber==1);
					
	path = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+MODULE_NAME_RR+ZK_PATH_SEP+"101"+ZK_PATH_SEP+ZK_ISACTIVE;
	ZkActiveValue zkActiveValue2 = ZkActiveValue("101");
	value = ZkActiveValue2Json(zkActiveValue2);

  cout<<"insertNode  path :"<<path<<" value :"<<value<<endl;
	zkWrapper.insertNode(path,value);
	
	sleep(3);//因为是多线程操作，所以需要等待一会儿
	suffixModuleInstanceNumber = resInfoZkClient.getSuffixModuleInstanceNumberByPrefixModule(MODULE_NAME_RR);
	cout<<"suffixModuleInstanceNumber  :"<<suffixModuleInstanceNumber<<endl;
	//assert(suffixModuleInstanceNumber==2);

	resInfoZkClient.unLoginr();
	cout<<"testClientSuffixModuleInstanceNumber  end !"<<endl;
	
	return 0;
}


int main(int argc, char *argv[]) {
	
	testExpiredHandler();
	
//	testClient();
	
//	testClientSuffixModuleInstanceNumber();

	return 0;
}

