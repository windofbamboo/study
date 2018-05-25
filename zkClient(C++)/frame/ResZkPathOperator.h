#ifndef RES_ZKPATH_OPERATOR_H_
#define RES_ZKPATH_OPERATOR_H_

#include <vector>
#include <map>
#include <cstdlib>
#include <unistd.h>

#include "baseUtil.h"
#include "ZkOutput.h"
#include "ZkActiveValue.h"
#include "ZkFileTypeValue.h"
#include "jsonUtil.h"
#include "ZkWrapper.h"

using namespace std;

class ResZkPathOperator 
{
protected:
	ZkWrapper& zkWrapper = ZkWrapper::GetInstance();
	bool 	isJsonValuePretty;
	string moduleName;
	string zkRoot;
	string channelId;
	
public:
	ResZkPathOperator();
	
	~ResZkPathOperator();
	
	ResZkPathOperator(const string zkConnectList, 
										const string zkRoot,
										const int redoTimes = -1,
										const int sleepTime = -1,
										SessionExpiredHandler handlerImp = NULL,
										void* context= NULL);

protected:		
	string getModuleInstanceRootPath(const string zkRoot,
																	const string moduleName,
																	const string channelId,
																	const string rootType);

	string getModuleRootPath(const string zkRoot, 
													const string moduleName, 
													const string rootType);
	
private:	
	vector<string> innerGetAllFileTypes(const string rootTypeEnum, 
																		const string zkRoot, 
																		const string moduleName,
																		const string channelId);
			
	vector<ZkOutput> innerGetValueByFileType(const string rootTypeEnum, 
																						const string valueType,
																						const string zkRoot, 
																						const string moduleName, 
																						const string channelId, 
																						const string fileType);	
		
	map<string,ZkOutput> innerGetValueByFileTypeWithSubDir(	const string rootTypeEnum, 
																													const string valueType,
																													const string zkRoot, 
																													const string moduleName, 
																													const string channelId, 
																													const string fileType);
		
	//string getSubWorkDir(const string path, int i);
	
	string getSubWorkPrefix(const string workPrefix, int i);
	
	void innerDeleteByFileType(	const string rootTypeEnum, 
															const string zkRoot, 
															const string moduleName,
															const string channelId, 
															const string fileType);
	
	void innerModifyOutputByFileType(	const string rootTypeEnum, 
																		const string valueType,
																		const string zkRoot, 
																		const string moduleName, 
																		const string channelId, 
																		const string fileType, 
																		vector<ZkOutput> &zkOutputOlds,
																		vector<ZkOutput> &zkOutputNews);
	
	void modifyOutputByFileTypeWithOneOutput(	const string outputRootPath, 
																						ZkOutput &zkOutputOld,
																						ZkOutput &zkOutputNew);
		
	void innerAddOutputByFileType(const string rootTypeEnum, 
																const string valueType,
																const string zkRoot, 
																const string moduleName, 
																const string channelId, 
																const string fileType, 
																vector<ZkOutput> &zkOutputs);
public:	

	ZkWrapper& getZkWrapper();
	
public:	
	
	vector<string> getAllFileTypes(const string zkRoot, 
																const string moduleName, 
																const string channelId);	
	
	int getModuleInstanceNumber(const string zkRoot, 
															const string moduleName);
	
	vector<string> getModuleInstanceList(const string zkRoot, 
																			const string moduleName);
	
	vector<string> getAllFileTypesByTaskState(	const string zkRoot, 
																						const string moduleName, 
																						const string channelId);
	
	vector<ZkOutput> getOutputByFileType(const string zkRoot, 
																			const string moduleName, 
																			const string channelId,
																			const string fileType);
			
	vector<ZkOutput> getErrOutByFileType(const string zkRoot, 
																				const string moduleName, 
																				const string channelId,
																				const string fileType);
			
	map<string,ZkOutput> getOutputByFileTypeWithSubDir(const string zkRoot, 
																										const string moduleName, 
																										const string channelId,
																										const string fileType);
								
	map<string,ZkOutput> getErrOutByFileTypeWithSubDir(const string zkRoot, 
																										const string moduleName,
																										const string channelId,
																										const string fileType);
	
	void deleteByFileType(const string zkRoot, 
												const string moduleName, 
												const string channelId, 
												const string fileType);
	
	void deleteByFileTypeByTaskState(	const string zkRoot, 
																		const string moduleName,
																		const string channelId,
																		const string fileType);

	void modifyOutputByFileType(const string zkRoot, 
															const string moduleName, 
															const string channelId,
															const string fileType, 
															vector<ZkOutput> &zkOutputOlds,
															vector<ZkOutput> &zkOutputNews);
	
	void modifyErrOutByFileType(const string zkRoot, 
															const string moduleName, 
															const string channelId,
															const string fileType, 
															vector<ZkOutput> &zkOutputOlds, 
															vector<ZkOutput> &zkOutputNews);

	void addOutputByFileType(	const string zkRoot, 
														const string moduleName, 
														const string channelId, 
														const string fileType,
														vector<ZkOutput> &zkOutputs);
			
	void addErrOutByFileType(	const string zkRoot, 
														const string moduleName, 
														const string channelId, 
														const string fileType,
														vector<ZkOutput> &zkOutputs);	

};

#endif /* RES_ZKPATH_OPERATOR_H_ */