#include <sstream>
#include "ResZkPathOperator.h"

ResZkPathOperator::ResZkPathOperator()
{
	isJsonValuePretty = true;
	moduleName = MODULE_NAME_UNKNOWN;
	zkWrapper = ZkWrapper::GetInstance();
}

ResZkPathOperator::~ResZkPathOperator()
{
}

ResZkPathOperator::ResZkPathOperator(const string zkConnectList, 
																		 const string zkRoot,
																		 const int redoTimes,
																		 const int sleepTime,
																		 SessionExpiredHandler handlerImp,
																		 void* context)
{
	isJsonValuePretty = true;
	moduleName = MODULE_NAME_UNKNOWN;
	zkWrapper = ZkWrapper::GetInstance();
	zkWrapper.Init(zkConnectList.c_str(), connectionTimeoutMs,zkRoot,redoTimes,sleepTime,handlerImp,context);
}

string ResZkPathOperator::getModuleInstanceRootPath(const string zkRoot, 
																										const string moduleName, 
																										const string channelId,
																										const string rootType)
{
	string result;
	if(rootType == ROOT_TYPE_TASKSTATE)
	{							
			result = zkRoot+ZK_PATH_SEP+ROOT_TYPE_TASKSTATE+ZK_PATH_SEP+moduleName+ZK_PATH_SEP+channelId;
	}
	else if(rootType == ROOT_TYPE_RESOURCEMANAGER)
	{	
			result = zkRoot+ZK_PATH_SEP+ROOT_TYPE_RESOURCEMANAGER;
	}
	else
	{									
			result = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+moduleName+ZK_PATH_SEP+channelId;
	}
	return result;
}
	
string ResZkPathOperator::getModuleRootPath(const string zkRoot, 
																						const string moduleName,
																						const string rootType)
{
	string result;
	if(rootType == ROOT_TYPE_TASKSTATE)
	{
			result = zkRoot+ZK_PATH_SEP+ROOT_TYPE_TASKSTATE+ZK_PATH_SEP+moduleName;
	}
	else if(rootType == ROOT_TYPE_RESOURCEMANAGER)
	{
			result = zkRoot+ZK_PATH_SEP+ROOT_TYPE_RESOURCEMANAGER;
	}
	else{
			result = zkRoot+ZK_PATH_SEP+ROOT_TYPE_REGISTER+ZK_PATH_SEP+moduleName;
	}
	return result;
}

vector<string> ResZkPathOperator::innerGetAllFileTypes(const string rootTypeEnum, 
																											const string zkRoot, 
																											const string moduleName,
																											const string channelId)
{
	string instanceRootPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum);
	string result = ZK_PATH_SEP+ ZK_ISACTIVE;
	
	vector<string> children; 
	try{
		children= zkWrapper.getChildNodeList(instanceRootPath);
	}catch(ZK_Exception e){
		#ifdef _DEBUG_
			if (e.getErrCode() == ExceptionErrCode::E_ZK_ZNONODE){
				cout<<"node is not exist :"<<instanceRootPath<<endl;
			}
		  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
		
	vector<string>::iterator  itr;
	for(itr=children.begin();itr!=children.end();)
	{
		 if( endsWith(*itr, result) || *itr == ZK_ISACTIVE)
		 {
				children.erase(itr);
		 }
		 else{
		 	itr++;
		 }
	}
	return children;
}

vector<string> ResZkPathOperator::getModuleInstanceList(const string zkRoot, 
																												const string moduleName)
{	
	string moduleRootPath = getModuleRootPath(zkRoot, moduleName, ROOT_TYPE_REGISTER);
	
	vector<string> children;
	
	try{
		children = zkWrapper.getChildNodeList(moduleRootPath);
		vector<string>::iterator  itr;
		for(itr=children.begin();itr!=children.end();)
		{
			string result = moduleRootPath+ZK_PATH_SEP+*itr+ZK_PATH_SEP+ZK_ISACTIVE;
			
			if(!zkWrapper.exist(result)){
				children.erase(itr);
			}
			else{
			 	itr++;
			}
		}
	}catch(ZK_Exception e){
		#ifdef _DEBUG_
			if (e.getErrCode() == ExceptionErrCode::E_ZK_ZNONODE){
				cout<<"node is not exist :"<<moduleRootPath<<endl;
			}
		  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
	return children;
}

//string ResZkPathOperator::getSubWorkDir(const string path, int i)
//{
//	stringstream result;
//	
//	if( path.length()>=0) {
//		if(!endsWith(path,ZK_PATH_SEP)){
//			result<<path<<ZK_PATH_SEP<<i;
//		}else{
//			result<<path<<i;
//		}
//	}else{
//		result<<path;
//	}
//	return result.str();
//}
	
string ResZkPathOperator::getSubWorkPrefix(const string workPrefix, int i)
{
	stringstream result;
	if(workPrefix.length()>=0) {
		result<<workPrefix<<"%"<<i<<"%";
	}else{
		result<<workPrefix;
	}
	return result.str();
}

vector<ZkOutput> ResZkPathOperator::innerGetValueByFileType(const string rootTypeEnum, 
																														const string valueType,
																														const string zkRoot, 
																														const string moduleName, 
																														const string channelId, 
																														const string fileType)
{
	string basePath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum);

	string outputPath=basePath+ZK_PATH_SEP+fileType+ZK_PATH_SEP+valueType;
	
	vector<ZkOutput> zkOutputs;
	try{
		vector<string> children = zkWrapper.getChildNodeList(outputPath);
		vector<string>::iterator  itr;
		for(itr=children.begin();itr!=children.end();itr++)
		{
			string childPath = outputPath+ZK_PATH_SEP+*itr;
			string value = zkWrapper.getNodeValue(*itr);
			
			ZkOutput zkOutput;
			if(Json2ZkOutput(value,zkOutput))
				zkOutputs.push_back(zkOutput);
		}
	}catch(ZK_Exception e){
		#ifdef _DEBUG_
		  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
	
	return zkOutputs;
}
			
map<string,ZkOutput> ResZkPathOperator::innerGetValueByFileTypeWithSubDir(const string rootTypeEnum, 
																																					const string valueType,
																																					const string zkRoot, 
																																					const string moduleName, 
																																					const string channelId, 
																																					const string fileType)
{
	string basePath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum);	
	string outputPath= basePath+ZK_PATH_SEP+fileType+ZK_PATH_SEP+valueType;
				
	map<string,ZkOutput> zkOutputs;
	vector<string> children;
	try{
	 	children = zkWrapper.getChildNodeList(outputPath);
	}catch(ZK_Exception e){
		#ifdef _DEBUG_
		  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
	
	vector<string>::iterator  itr;
	for(itr=children.begin();itr!=children.end();itr++)
	{
		string childPath=outputPath+ZK_PATH_SEP+*itr;
		string node;
		try{
			node = zkWrapper.getNodeValue(childPath);
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
			  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
			#endif
			continue;
		}
		ZkOutput zkOutput;
		if(!Json2ZkOutput(node,zkOutput)){
			continue;
		}

		if(zkOutput.getOutType()==OUT_TYPE_SUB_DIR)
		{
			for(int i=0;i<zkOutput.getOutSplitNum();i++) {
				string subPath=outputPath+ZK_PATH_SEP+*itr;
				string subNode;
				try{
					subNode = zkWrapper.getNodeValue(subPath);
				}catch(ZK_Exception e){
					#ifdef _DEBUG_
					  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
					#endif
					continue;
				}
				ZkOutput zkOutputSub;
				if(Json2ZkOutput(subNode,zkOutputSub))
					zkOutputs.insert(map<string,ZkOutput>::value_type(subNode,zkOutputSub));
			}
		}
		else {
					zkOutputs.insert(map<string,ZkOutput>::value_type(node,zkOutput));
		}
	}
	return zkOutputs;
}
		
void ResZkPathOperator::innerDeleteByFileType(const string rootTypeEnum, 
																							const string zkRoot, 
																							const string moduleName,
																							const string channelId, 
																							const string fileType)
{
	string basePath   = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum);
	string outputPath = basePath+ZK_PATH_SEP+fileType;

	try{
		zkWrapper.deleteNode(outputPath);
	}catch(ZK_Exception e){
		#ifdef _DEBUG_
		  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
}
	
void ResZkPathOperator::innerModifyOutputByFileType(const string rootTypeEnum, 
																										const string valueType,
																										const string zkRoot, 
																										const string moduleName, 
																										const string channelId, 
																										const string fileType, 
																										vector<ZkOutput> &zkOutputOlds,
																										vector<ZkOutput> &zkOutputNews)
{
	string basePath  = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum);
	string outputPath= basePath+ZK_PATH_SEP+fileType+ZK_PATH_SEP+valueType;
	
	bool isFound;
	// 删除已经剔除的目录
	vector<ZkOutput>::iterator  itrOld;
	vector<ZkOutput>::iterator  itrNew;
	for(itrOld=zkOutputOlds.begin();itrOld!=zkOutputOlds.end();itrOld++)
	{			
		string subPath=outputPath+ZK_PATH_SEP+itrOld->getDirId();
		
		isFound = false;
		for(itrNew=zkOutputNews.begin();itrNew!=zkOutputNews.end();itrNew++)
		{
			if(itrOld->getDirId() == itrNew->getDirId())
			{
				isFound = true;
				if((*itrOld) == (*itrNew)){
					modifyOutputByFileTypeWithOneOutput(subPath,*itrOld, *itrNew);
				}
				break;
			}
			if (!isFound) {// 删除已经剔除的目录
				try{
					zkWrapper.deleteNode(subPath);
				}catch(ZK_Exception e){
					#ifdef _DEBUG_
					  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
					#endif
				}
			}
		}
	}

	vector<ZkOutput> zkOutputAdds;
	for(itrNew=zkOutputNews.begin();itrNew!=zkOutputNews.end();itrNew++)
	{
		isFound = false;
		for(itrOld=zkOutputOlds.begin();itrOld!=zkOutputOlds.end();itrOld++)
		{
			if(itrOld->getDirId() == itrNew->getDirId()) {
				isFound = true;
				break;
			}
		}
		if (!isFound) {// 增加新增的目录
				zkOutputAdds.push_back(*itrNew);
		}
	}

	if (!zkOutputAdds.empty())
			innerAddOutputByFileType(rootTypeEnum, valueType, zkRoot, moduleName, channelId, fileType, zkOutputAdds);
}			

void ResZkPathOperator::modifyOutputByFileTypeWithOneOutput(const string outputRootPath, 
																														ZkOutput &zkOutputOld,
																														ZkOutput &zkOutputNew)
{			
	string outputPath       = outputRootPath;
	string dirIdNew         = zkOutputNew.getDirId();
	string pathNew          = zkOutputNew.getPath();
	string subWorDirNew     = zkOutputNew.getWorkPath();
	string subWorkPrefixNew = zkOutputNew.getWorkPrefix();
	int    splitNumNew      = zkOutputNew.getOutSplitNum();
	
	string value = ZkOutput2Json(zkOutputNew);
	
	stringstream tempPath;

		// 输出类型不变的处理
		if (zkOutputOld.getOutType() == zkOutputNew.getOutType()) {
			// 普通目录直接更新
			if (zkOutputOld.getOutType() == OUT_TYPE_NORMAL_DIR) {
				try{
					zkWrapper.updateNodeValue(outputPath, value);
				}catch(ZK_Exception e){
					#ifdef _DEBUG_
					  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
					#endif
				}
			} else {// OUT_TYPE_SUB_DIR
					// 子目录需要区分拆分字目录数大小
				if (zkOutputOld.getOutSplitNum() > zkOutputNew.getOutSplitNum() ) {
					for (int i = splitNumNew; i < zkOutputOld.getOutSplitNum(); i++) {
						tempPath.str("");
						tempPath<<outputPath<<ZK_PATH_SEP<<i;
						string subPath=	tempPath.str();		
						try{
							zkWrapper.deleteNode(subPath);
						}catch(ZK_Exception e){
							#ifdef _DEBUG_
							  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
							#endif
						}
					}
					for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {

						tempPath.str("");
						tempPath<<dirIdNew<<"."<<i;
						zkOutputNew.setDirId      (tempPath.str());
						
						tempPath.str("");
						tempPath<<pathNew<<ZK_PATH_SEP<<i;
						zkOutputNew.setPath       (tempPath.str());
						zkOutputNew.setOutType    (OUT_TYPE_NORMAL_DIR);
						zkOutputNew.setWorkPath   (zkOutputNew.getWorkPathFromPath());
						zkOutputNew.setWorkPrefix (getSubWorkPrefix(subWorkPrefixNew, i));
						zkOutputNew.setOutSplitNum(0);
						
						tempPath.str("");
						tempPath<<outputPath<<ZK_PATH_SEP<<i;
						
						string subPath = tempPath.str();
						string value   = ZkOutput2Json(zkOutputNew);
						try{
							zkWrapper.updateNodeValue(subPath, value);
						}catch(ZK_Exception e){
							#ifdef _DEBUG_
							  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
							#endif
						}
					}
				} 
				else if (zkOutputOld.getOutSplitNum() < zkOutputNew.getOutSplitNum()) {
					for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {

						tempPath.str("");
						tempPath<<dirIdNew<<"."<<i;
						zkOutputNew.setDirId      (tempPath.str());

						tempPath.str("");
						tempPath<<pathNew<<ZK_PATH_SEP<<i;
						zkOutputNew.setPath       (tempPath.str());
						zkOutputNew.setOutType    (OUT_TYPE_NORMAL_DIR);
						zkOutputNew.setWorkPath   (zkOutputNew.getWorkPathFromPath());
						zkOutputNew.setWorkPrefix (getSubWorkPrefix(subWorkPrefixNew, i));
						zkOutputNew.setOutSplitNum(0);

						tempPath.str("");
						tempPath<<outputPath<<ZK_PATH_SEP<<i;
							
						string subPath = tempPath.str();
						string value   = ZkOutput2Json(zkOutputNew);
						
						if (i >= zkOutputOld.getOutSplitNum()) {
							try{
								zkWrapper.insertNode(subPath, value);
							}catch(ZK_Exception e){
								#ifdef _DEBUG_
								  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
								#endif
							}
						} else {
							try{
								zkWrapper.updateNodeValue(subPath,value);
							}catch(ZK_Exception e){
								#ifdef _DEBUG_
								  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
								#endif
							}
						}
					}
				} 
				else {
					for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {
						
						tempPath.str("");
						tempPath<<dirIdNew<<"."<<i;
						zkOutputNew.setDirId     (tempPath.str());
						
						tempPath.str("");
						tempPath<<pathNew<<ZK_PATH_SEP<<i;
						zkOutputNew.setPath       (tempPath.str());
						zkOutputNew.setOutType    (OUT_TYPE_NORMAL_DIR);
						zkOutputNew.setWorkPath   (zkOutputNew.getWorkPathFromPath());
						zkOutputNew.setWorkPrefix (getSubWorkPrefix(subWorkPrefixNew, i));
						zkOutputNew.setOutSplitNum(0);
						
						tempPath.str("");
						tempPath<<outputPath<<ZK_PATH_SEP<<i;
						
            string subPath = tempPath.str();
						string value   = ZkOutput2Json(zkOutputNew);

						try{
							zkWrapper.updateNodeValue(subPath, value);
						}catch(ZK_Exception e){
							#ifdef _DEBUG_
							  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
							#endif
						}
					}
				}
				zkOutputNew.setDirId      (dirIdNew);
				zkOutputNew.setPath       (pathNew);
				zkOutputNew.setOutType    (OUT_TYPE_SUB_DIR);
				zkOutputNew.setWorkPath   (subWorDirNew);
				zkOutputNew.setWorkPrefix (subWorkPrefixNew);
				zkOutputNew.setOutSplitNum(splitNumNew);
			}
			return;
		}
		/// 不相同的处理
		/// 先更新子目录，再更新普通目录
		if (zkOutputNew.getOutType() == OUT_TYPE_SUB_DIR) {
			// 子目录需要插入,此时zkOutputOld没有子目录
			for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {

        tempPath.str("");
				tempPath<<dirIdNew<<"."<<i;
				zkOutputNew.setDirId     (tempPath.str());

				tempPath.str("");
				tempPath<<pathNew<<ZK_PATH_SEP<<i;
				zkOutputNew.setPath       (tempPath.str());
				zkOutputNew.setOutType    (OUT_TYPE_NORMAL_DIR);
				zkOutputNew.setWorkPath   (zkOutputNew.getWorkPathFromPath());
				zkOutputNew.setWorkPrefix (getSubWorkPrefix(subWorkPrefixNew, i));

				tempPath.str("");
				tempPath<<outputPath<<ZK_PATH_SEP<<i;

        string subPath = tempPath.str();
				string value   = ZkOutput2Json(zkOutputNew);

				try{
					zkWrapper.insertNode(subPath, value);
				}catch(ZK_Exception e){
					#ifdef _DEBUG_
					  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
					#endif
				}
			}
			zkOutputNew.setDirId      (dirIdNew);
			zkOutputNew.setPath       (pathNew);
			zkOutputNew.setOutType    (OUT_TYPE_NORMAL_DIR);
			zkOutputNew.setWorkPath   (subWorDirNew);
			zkOutputNew.setWorkPrefix (subWorkPrefixNew);
			zkOutputNew.setOutSplitNum(splitNumNew);
		}
		if (zkOutputOld.getOutType() == OUT_TYPE_SUB_DIR) {
			// 子目录需要删除,此时zkOutputNew没有子目录
			for (int i = 0; i < zkOutputOld.getOutSplitNum(); i++) {

        tempPath.str("");
				tempPath<<outputPath<<ZK_PATH_SEP<<i;
				
				string subPath=tempPath.str();
				try{
					zkWrapper.deleteNode(subPath);
				}catch(ZK_Exception e){
					#ifdef _DEBUG_
					  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
					#endif
				}
			}
		}
		//更新普通目录以及子目录的父目录
		try{
			zkWrapper.updateNodeValue(outputPath, value);
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
			  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
			#endif
		}
}		

void ResZkPathOperator::innerAddOutputByFileType(	const string rootTypeEnum, 
																									const string valueType,
																									const string zkRoot, 
																									const string moduleName, 
																									const string channelId, 
																									const string fileType, 
																									vector<ZkOutput> &zkOutputs)
{
	string basePath  = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum);
	string outputPath= basePath+ZK_PATH_SEP+fileType+ZK_PATH_SEP+valueType;
	stringstream tempValue;
	/// 先更新子目录，再更新普通目录
	// 带子目录的父目录也需要更新
	try{
		zkWrapper.insertNode(outputPath);
	}catch(ZK_Exception e){
		#ifdef _DEBUG_
		  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
		#endif
	}
		
	vector<ZkOutput>::iterator  itr;
	for(itr=zkOutputs.begin();itr!=zkOutputs.end();itr++)
	{
		if (itr->getOutType() == OUT_TYPE_SUB_DIR) 
		{
			string dirIdNew         = itr->getDirId();
			string pathNew          = itr->getPath();
			string subWorDirNew     = itr->getWorkPath();
			string subWorkPrefixNew = itr->getWorkPrefix();
			int    splitNumNew      = itr->getOutSplitNum();
			
			for (int i = 0; i < splitNumNew; i++) {

        tempValue.str("");
				tempValue<<dirIdNew<<"."<<i;
				itr->setDirId 		 (tempValue.str());
				
				tempValue.str("");
				tempValue<<pathNew<<ZK_PATH_SEP<<i;
				itr->setPath       (tempValue.str());
				itr->setOutType    (OUT_TYPE_NORMAL_DIR);
				itr->setWorkPath   (itr->getWorkPathFromPath());
				itr->setWorkPrefix (getSubWorkPrefix(subWorkPrefixNew,i));
				itr->setOutSplitNum(0);

				string value   = ZkOutput2Json(*itr);
				tempValue.str("");
				tempValue<<pathNew<<ZK_PATH_SEP<<itr->getDirId()<<ZK_PATH_SEP<<i;
				string subPath = tempValue.str();
				
				try{
					zkWrapper.insertNode(subPath,value);
				}catch(ZK_Exception e){
					#ifdef _DEBUG_
					  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
					#endif
				}
			}
			itr->setDirId 		 (dirIdNew);
			itr->setPath 			 (pathNew);
			itr->setOutType 	 (OUT_TYPE_SUB_DIR);
			itr->setWorkPath 	 (subWorDirNew);
			itr->setWorkPrefix (subWorkPrefixNew);
			itr->setOutSplitNum(0);
		}
		
		string value  = ZkOutput2Json(*itr);

		tempValue.str("");
		tempValue<<outputPath<<ZK_PATH_SEP<<itr->getDirId();
		string subPath = tempValue.str();

		try{
			zkWrapper.insertNode(subPath,value);
		}catch(ZK_Exception e){
			#ifdef _DEBUG_
			  cout<<"errCode :"<<e.getErrCode()<<", errMsg :"<<ExceptionErrCode::GetMsgByCode(e.getErrCode())<<__FILE__<<__LINE__<<endl;
			#endif
		}
	}
}


ZkWrapper& ResZkPathOperator::getZkWrapper()
{
	return zkWrapper;
}

vector<string> ResZkPathOperator::getAllFileTypes(const string zkRoot, 
																									const string moduleName, 
																									const string channelId)
{
	return innerGetAllFileTypes(ROOT_TYPE_REGISTER, zkRoot, moduleName, channelId);
}
	
int ResZkPathOperator::getModuleInstanceNumber(const string zkRoot, 
																							 const string moduleName)
{
	vector<string> nodeVec = getModuleInstanceList(zkRoot,moduleName);
	return nodeVec.size();
}

vector<string> ResZkPathOperator::getAllFileTypesByTaskState(	const string zkRoot, 
																															const string moduleName, 
																															const string channelId)
{	
	return innerGetAllFileTypes(ROOT_TYPE_TASKSTATE, zkRoot, moduleName, channelId);
}

vector<ZkOutput> ResZkPathOperator::getOutputByFileType(const string zkRoot, 
																												const string moduleName, 
																												const string channelId,
																												const string fileType)
{
	return innerGetValueByFileType(ROOT_TYPE_REGISTER, VALUE_TYPE_OUTPUT,
																 zkRoot, moduleName, channelId, fileType);
}

vector<ZkOutput> ResZkPathOperator::getErrOutByFileType(const string zkRoot, 
																												const string moduleName, 
																												const string channelId,
																												const string fileType)
{
	return innerGetValueByFileType(ROOT_TYPE_REGISTER, VALUE_TYPE_ERROUT,
				zkRoot, moduleName, channelId, fileType);
}

	
map<string,ZkOutput> ResZkPathOperator::getOutputByFileTypeWithSubDir(const string zkRoot, 
																																			const string moduleName, 
																																			const string channelId,
																																			const string fileType)
{
	return innerGetValueByFileTypeWithSubDir(ROOT_TYPE_REGISTER, VALUE_TYPE_OUTPUT,
				zkRoot, moduleName, channelId, fileType);
}
								
map<string,ZkOutput> ResZkPathOperator::getErrOutByFileTypeWithSubDir(const string zkRoot, 
																																			const string moduleName, 
																																			const string channelId,
																																			const string fileType)
{
	return innerGetValueByFileTypeWithSubDir(ROOT_TYPE_REGISTER, VALUE_TYPE_ERROUT,
				zkRoot, moduleName, channelId, fileType);
}					
	
void ResZkPathOperator::deleteByFileType(	const string zkRoot, 
																					const string moduleName, 
																					const string channelId, 
																					const string fileType)
{
	innerDeleteByFileType(ROOT_TYPE_REGISTER, zkRoot, moduleName, channelId, fileType);
}

void ResZkPathOperator::deleteByFileTypeByTaskState(const string zkRoot, 
																										const string moduleName, 
																										const string channelId,
																										const string fileType)
{
	innerDeleteByFileType(ROOT_TYPE_REGISTER, zkRoot, moduleName, channelId, fileType);
}	

void ResZkPathOperator::modifyOutputByFileType(	const string zkRoot, 
																								const string moduleName, 
																								const string channelId,
																								const string fileType, 
																								vector<ZkOutput> &zkOutputOlds, 
																								vector<ZkOutput> &zkOutputNews)
{
	innerModifyOutputByFileType(ROOT_TYPE_REGISTER, VALUE_TYPE_OUTPUT, zkRoot,
				moduleName, channelId, fileType, zkOutputOlds, zkOutputNews);
}

void ResZkPathOperator::modifyErrOutByFileType(	const string zkRoot, 
																								const string moduleName, 
																								const string channelId,
																								const string fileType, 
																								vector<ZkOutput> &zkOutputOlds, 
																								vector<ZkOutput> &zkOutputNews)
{
	innerModifyOutputByFileType(ROOT_TYPE_REGISTER, VALUE_TYPE_ERROUT, zkRoot,
				moduleName, channelId, fileType, zkOutputOlds, zkOutputNews);
}

void ResZkPathOperator::addOutputByFileType(const string zkRoot, 
																						const string moduleName, 
																						const string channelId, 
																						const string fileType,
																						vector<ZkOutput> &zkOutputs)
{
	innerAddOutputByFileType(ROOT_TYPE_REGISTER, VALUE_TYPE_OUTPUT, zkRoot,
				moduleName, channelId, fileType, zkOutputs);
}

void ResZkPathOperator::addErrOutByFileType(const string zkRoot, 
																						const string moduleName, 
																						const string channelId, 
																						const string fileType,
																						vector<ZkOutput> &zkOutputs)
{
	innerAddOutputByFileType(ROOT_TYPE_REGISTER, VALUE_TYPE_ERROUT, zkRoot,
				moduleName, channelId, fileType, zkOutputs);
}


