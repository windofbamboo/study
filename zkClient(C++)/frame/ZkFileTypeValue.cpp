#include "ZkFileTypeValue.h"

ZkFileTypeValue::ZkFileTypeValue()
{
	fileType ="";
	zkOutputs.clear();
	zkErrOuts.clear();
}

ZkFileTypeValue::ZkFileTypeValue(const ZkFileTypeValue& right)
{
  *this = right;
}

ZkFileTypeValue::~ZkFileTypeValue()
{
}

void ZkFileTypeValue::operator = (const ZkFileTypeValue& right)
{
  if(this != &right)
  {
  	fileType =right.fileType;
		zkOutputs=right.zkOutputs;
		zkErrOuts=right.zkErrOuts;
  }
}

bool operator==(const ZkFileTypeValue &left,const ZkFileTypeValue& right)
{
  return (left.fileType == right.fileType);
}

bool operator<(const ZkFileTypeValue &left,const ZkFileTypeValue& right)
{
  return (left.fileType < right.fileType);
}

std::ostream& operator<<(std::ostream& os,const ZkFileTypeValue& po)
{
	int i=0,j=0;
	std::vector<ZkOutput> zkOutputs = po.zkOutputs;
	std::vector<ZkOutput> zkErrOuts = po.zkErrOuts;

  os<<"{fileType:" << po.fileType	<<","<<
      "zkOutputs:" <<"{";
      
      for(std::vector<ZkOutput>::iterator itr=zkOutputs.begin();itr!=zkOutputs.end();itr++){
      	if(i>0){
      		os<<",";
      	}
  	os<<"{fileType:"		<< itr->getFileType()	<<","<<
				"hostOrIp:"			<< itr->getHostOrIp()	<<","<<
				"dirId:"				<< itr->getDirId()		<<","<<
				"path:"					<< itr->getPath()			<<","<<
				"servType:"			<< itr->getServType()	<<","<<
				"outType:"			<< itr->getOutType()	<<","<<
				"getOrPut:"			<< itr->getGetOrPut()	<<","<<
				"outSplitNum:"	<< itr->getOutSplitNum()<<","<<
				"concWorkPath:"	<< itr->getConcWorkPath()<<","<<
				"workPath:"			<< itr->getWorkPath()	<<","<<
				"workPrefix:"		<< itr->getWorkPrefix()<<","<<
				"loginUser:"		<< itr->getLoginUser()	<<","<<
				"loginPassword:"<< itr->getLoginPassword()<<"}";
				i++;
  		}
  		os<<"},"<<"zkErrOuts:" ;
  		
      for(std::vector<ZkOutput>::iterator itr=zkErrOuts.begin();itr!=zkErrOuts.end();itr++){
      	if(j>0){
      		os<<",";
      	}
  		 	os<<"{fileType:"<< itr->getFileType()	<<","<<
				"hostOrIp:"			<< itr->getHostOrIp()	<<","<<
				"dirId:"				<< itr->getDirId()		<<","<<
				"path:"					<< itr->getPath()			<<","<<
				"servType:"			<< itr->getServType()	<<","<<
				"outType:"			<< itr->getOutType()	<<","<<
				"getOrPut:"			<< itr->getGetOrPut()	<<","<<
				"outSplitNum:"	<< itr->getOutSplitNum()<<","<<
				"concWorkPath:"	<< itr->getConcWorkPath()<<","<<
				"workPath:"			<< itr->getWorkPath()<<","<<
				"workPrefix:"		<< itr->getWorkPrefix()<<","<<
				"loginUser:"		<< itr->getLoginUser()	<<","<<
				"loginPassword:"<< itr->getLoginPassword()<<"}";
				j++;
  		}
  		os<<"}"<<endl;
      
  return os;
}


string ZkFileTypeValue::getFileType() {
	return fileType;
}

void ZkFileTypeValue::setFileType(string fileType) {
	this->fileType = fileType;
}

vector<ZkOutput> ZkFileTypeValue::getZkOutputs() {
	return zkOutputs;
}

void ZkFileTypeValue::setZkOutputs(vector<ZkOutput> zkOutputs ) {
	this->zkOutputs = zkOutputs;
}

vector<ZkOutput> ZkFileTypeValue::getZkErrOuts() {
	return zkErrOuts;
}

void ZkFileTypeValue::setZkErrOuts(vector<ZkOutput> zkErrOuts ) {
	this->zkErrOuts = zkErrOuts;
}