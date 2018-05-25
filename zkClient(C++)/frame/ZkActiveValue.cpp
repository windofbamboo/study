#include <unistd.h>
#include <stdio.h>
#include <sstream>

#include "ZkEnum.h"
#include "ZkActiveValue.h"

ZkActiveValue::ZkActiveValue()
{
	hostOrIp ="";
	procId   ="";
	channelId="";
}

ZkActiveValue::ZkActiveValue(const ZkActiveValue& right)
{
  *this = right;
}

ZkActiveValue::ZkActiveValue(const string channelId)
{
	char hostname[32];
	if( gethostname(hostname,sizeof(hostname)) )
	{
	  this->hostOrIp  = "localhost";
	  #ifdef _DEBUG_
      cout<<"gethostname fail!"<<__FILE__<<__LINE__<<endl;
    #endif
	}else{
		this->hostOrIp  = hostname;
	}

	stringstream stream;
	stream<<getpid();

	this->procId    = stream.str();
	this->channelId = channelId;
	
	stream.clear();
	stream.str("");
}

ZkActiveValue::~ZkActiveValue()
{
}

void ZkActiveValue::operator = (const ZkActiveValue& right)
{
  if(this != &right)
  {
  	hostOrIp  = right.hostOrIp;
		procId    = right.procId;
		channelId = right.channelId;
  }
}

bool operator==(const ZkActiveValue &left,const ZkActiveValue& right)
{
  return (left.hostOrIp == right.hostOrIp && 
  			  left.procId 	== right.procId 	&&
  			  left.channelId== right.channelId);
}

bool operator<(const ZkActiveValue &left,const ZkActiveValue& right)
{
	if(left.hostOrIp < right.hostOrIp) return true;
  if(left.hostOrIp > right.hostOrIp) return false;
  	
  if(left.procId < right.procId) return true;
  if(left.procId > right.procId) return false;			

  return (left.channelId < right.channelId);
}

std::ostream& operator<<(std::ostream& os,const ZkActiveValue& po)
{
  os<<"{hostOrIp:"<< po.hostOrIp	<<","<<
      "procId:"		<< po.procId		<<","<<
      "channelId:"<< po.channelId	<<"}"<<endl;
  return os;
}


string ZkActiveValue::getHostOrIp() {
	return hostOrIp;
}

void ZkActiveValue::setHostOrIp(string hostOrIp) {
	this->hostOrIp = hostOrIp;
}

string ZkActiveValue::getChannelId() {
	return channelId;
}

void ZkActiveValue::setChannelId(string channelId) {
	this->channelId = channelId;
}

string ZkActiveValue::getProcId() {
	return procId;
}

void ZkActiveValue::setProcId(string procId) {
	this->procId = procId;
}