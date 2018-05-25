#ifndef ZK_ACTIVE_VALUE_H_
#define ZK_ACTIVE_VALUE_H_

#include <iostream>
#include <string>

using namespace std;

class ZkActiveValue 
{
private:
	string hostOrIp;
	string procId;
	string channelId;

public:
	
	ZkActiveValue();
  ZkActiveValue(const ZkActiveValue& right);
  ZkActiveValue(const string channelId);

  ~ZkActiveValue();	

	void operator = (const ZkActiveValue& right);

  friend bool operator == (const ZkActiveValue &left,const ZkActiveValue& right);

	friend bool operator < (const ZkActiveValue &left,const ZkActiveValue& right);

  friend std::ostream& operator<<(std::ostream& os,const ZkActiveValue& po);
  	
public:
	  	
  string getHostOrIp();

	void setHostOrIp(string hostOrIp) ;
	
	string getChannelId();
	
	void setChannelId(string channelId);
	
	string getProcId();
	
	void setProcId(string procId);

};

#endif /* ZK_ACTIVE_VALUE_H_ */
