#include "ZkEnum.h"
#include "baseUtil.h"
#include "ZkOutput.h"

ZkOutput::ZkOutput()
{
		fileType			="";
		hostOrIp			="";
		port          =0;
		dirId					="";
		path					="";
		servType			=0;
		outType				=OUT_TYPE_NORMAL_DIR;
		getOrPut			=SSFTP_GET_FOR_FTP;
		outSplitNum		=0;
		concWorkPath	="";
		workPath			="";
		workPrefix		="";
		globFnameMatch="";
		loginUser			="";
		loginPassword	="";
}

ZkOutput::ZkOutput(const ZkOutput& right)
{
  *this = right;
}

ZkOutput::~ZkOutput()
{
}

void ZkOutput::operator = (const ZkOutput& right)
{
  if(this != &right)
  {
  	fileType			=right.fileType;
		hostOrIp			=right.hostOrIp;
		port					=right.port;
		dirId					=right.dirId;
		path					=right.path;
		servType			=right.servType;
		outType				=right.outType;
		getOrPut			=right.getOrPut;
		outSplitNum		=right.outSplitNum;
		concWorkPath	=right.concWorkPath;
		workPath			=right.workPath;
		workPrefix		=right.workPrefix;
		globFnameMatch=right.globFnameMatch;
		loginUser			=right.loginUser;
		loginPassword	=right.loginPassword;
  }    
}

bool operator==(const ZkOutput &left,const ZkOutput& right)
{
  return (left.concWorkPath == right.concWorkPath && 
    			left.dirId 			  == right.dirId 		&&
    			left.fileType 		== right.fileType &&
    			left.getOrPut 		== right.getOrPut && 
    			left.globFnameMatch== right.globFnameMatch &&
    			left.hostOrIp 		== right.hostOrIp &&
    			left.loginPassword== right.loginPassword 	&&
    			left.loginUser 	  == right.loginUser 		 	&&
    			left.outSplitNum 	== right.outSplitNum 		&&
    			left.outType 			== right.outType 	&& 
    			left.path 				== right.path 		&& 
    			left.port					== right.port 		&& 
    			left.servType 		== right.servType &&
    			left.workPath 		== right.workPath &&
    			left.workPrefix 	== right.workPrefix);
}

bool operator<(const ZkOutput &left,const ZkOutput& right)
{
  if(left.concWorkPath < right.concWorkPath) return true;
  if(left.concWorkPath > right.concWorkPath) return false;
  
  if(left.dirId < right.dirId) return true;
  if(left.dirId > right.dirId) return false;
  
  if(left.fileType < right.fileType) return true;
  if(left.fileType > right.fileType) return false;
  
  if(left.getOrPut < right.getOrPut) return true;
  if(left.getOrPut > right.getOrPut) return false;
  	
  if(left.globFnameMatch < right.globFnameMatch) return true;
  if(left.globFnameMatch > right.globFnameMatch) return false;	
  	
  if(left.hostOrIp < right.hostOrIp) return true;
  if(left.hostOrIp > right.hostOrIp) return false;	
  	
  if(left.loginPassword < right.loginPassword) return true;
  if(left.loginPassword > right.loginPassword) return false;
  	
  if(left.loginUser < right.loginUser) return true;
  if(left.loginUser > right.loginUser) return false;
  
  if(left.outSplitNum < right.outSplitNum) return true;
  if(left.outSplitNum > right.outSplitNum) return false;
  
  if(left.outType < right.outType) return true;
  if(left.outType > right.outType) return false;
  
  if(left.path < right.path) return true;
  if(left.path > right.path) return false;
  	
  if(left.port < right.port) return true;
  if(left.port > right.port) return false;
  	
  if(left.servType < right.servType) return true;
  if(left.servType > right.servType) return false;
  	
  if(left.workPath < right.workPath) return true;
  if(left.workPath > right.workPath) return false;

  return (left.workPrefix < right.workPrefix);
}

std::ostream& operator<<(std::ostream& os,const ZkOutput& po)
{
  os<<"{fileType:"		<< po.fileType	<<","<<
			"hostOrIp:"			<< po.hostOrIp	<<","<<
			"port:"					<< po.port	<<","<<
			"dirId:"				<< po.dirId			<<","<<
			"path:"					<< po.path			<<","<<
			"servType:"			<< po.servType	<<","<<
			"outType:"			<< po.outType		<<","<<
			"getOrPut:"			<< po.getOrPut	<<","<<
			"outSplitNum:"	<< po.outSplitNum	<<","<<
			"concWorkPath:"	<< po.concWorkPath<<","<<
			"workPath:"			<< po.workPath	<<","<<
			"workPrefix:"		<< po.workPrefix<<","<<
			"globFnameMatch:"<< po.globFnameMatch<<","<<
			"loginUser:"		<< po.loginUser	<<","<<
			"loginPassword:"<< po.loginPassword	<<"}"<<endl;
  return os;
}

bool ZkOutput::isPutPath(){
	if(this->path == "")
		return false;
	
	return (this->path.find(PUT_PATH_SEP, 0) !=-1);	
}

void ZkOutput::instancePath() 
{
	this->path=getTruePathByEnv(path);
	this->concWorkPath=getTruePathByEnv(concWorkPath);
	this->workPath=getTruePathByEnv(workPath);
}

string ZkOutput::getPutRemotePath() {
	if(isPutPath()) {
		vector<string> dirVec = stringSplit(path, PUT_PATH_SEP); 
		return dirVec[1];
	}else {
		return "";
	}
}
	
void ZkOutput::setPutRemotePath(string remotePath) {
	if(isPutPath()) {
		vector<string> dirVec = stringSplit(path, PUT_PATH_SEP); 
		path=dirVec[0]+ZK_PATH_SEP+remotePath;
	}else {
		path=path+ZK_PATH_SEP+remotePath;
	}
}

string ZkOutput::getFileType(){
	return fileType;
}

void ZkOutput::setFileType(string fileType){
	this->fileType = fileType;
}

string ZkOutput::getDirId(){
	return dirId;
}

void ZkOutput::setDirId(string dirId){
	this->dirId = dirId;
}

string ZkOutput::getHostOrIp(){
	return hostOrIp;
}
	
void ZkOutput::setHostOrIp(string hostOrIp){
	this->hostOrIp = hostOrIp;
}

int ZkOutput::getPort(){
	return port;
}
	
void ZkOutput::setPort(int port){
	this->port = port;
}

string ZkOutput::getPath() {
	if(isPutPath()) {
		vector<string> dirVec = stringSplit(path, PUT_PATH_SEP); 
		return dirVec[0];
	}else {
		return path;
	}
}

void ZkOutput::setPath(string path) {
	if(isPutPath()) {
		this->path=this->path+path;
	}else {
		this->path = path;
	}
}

string ZkOutput::getWorkPathFromPath() {
	
	if(isPutPath()) {
		vector<string> dirVec = stringSplit(path, PUT_PATH_SEP); 
		
		if(dirVec.size()==2) {//Ô¶¶ËµÄÁÙÊ±Ä¿Â¼
			return dirVec[1]+ZK_PATH_SEP+"work";
		}else {
			if(dirVec[0]=="")
				return "";
			return dirVec[0]+ZK_PATH_SEP+"work";
		}
	}else {
		if(path=="") {
			return "";
		}else {
			return path+ZK_PATH_SEP+"work";
		}
	}
}

int ZkOutput::getServType() {
	return servType;
}

void ZkOutput::setServType(int servType) {
	this->servType = servType;
}

int ZkOutput::getOutType(int splitNum) {
	if(splitNum<=1)
		return OUT_TYPE_NORMAL_DIR;
	else
		return OUT_TYPE_SUB_DIR;
}

int ZkOutput::getOutType() {
	return outType;
}

void ZkOutput::setOutType(int outType) {
	this->outType = outType;
}

int ZkOutput::getGetOrPut() {
	return getOrPut;
}

void ZkOutput::setGetOrPut(int getOrPut) {
	this->getOrPut = getOrPut;
}

int ZkOutput::getOutSplitNum() {
	return outSplitNum;
}
void ZkOutput::setOutSplitNum(int outSplitNum) {
	this->outSplitNum = outSplitNum;
	int outType = getOutType(outSplitNum);
	
	setOutType(outType);
}

string ZkOutput::getConcWorkPath() {
	return concWorkPath;
}

void ZkOutput::setConcWorkPath(string concWorkPath) {
	this->concWorkPath = concWorkPath;
}

string ZkOutput::getWorkPath() {
	return workPath;
}
void ZkOutput::setWorkPath(string workPath) {
	this->workPath = workPath;
}

string ZkOutput::getWorkPrefix() {
	return workPrefix;
}

void ZkOutput::setWorkPrefix(string workPrefix) {
	this->workPrefix = workPrefix;
}

string ZkOutput::getGlobFnameMatch() {
	return globFnameMatch;
}

void ZkOutput::setGlobFnameMatch(string globFnameMatch) {
	this->globFnameMatch = globFnameMatch;
}

string ZkOutput::getLoginUser() {
	return loginUser;
}

void ZkOutput::setLoginUser(string loginUser) {
	this->loginUser = loginUser;
}

string ZkOutput::getLoginPassword() {
	return loginPassword;
}

void ZkOutput::setLoginPassword(string loginPassword) {
	this->loginPassword = loginPassword;
}

