#ifndef ZK_OUTPUT_H_
#define ZK_OUTPUT_H_

#include <iostream>

using namespace std;

class ZkOutput 
{
public:
  ZkOutput();
  ZkOutput(const ZkOutput& right);

  ~ZkOutput();

private:
	
	string	fileType;
	string	hostOrIp;
	int 	  port;
	string	dirId;
	string	path;
	int     servType;
	int     outType;
	int     getOrPut;
	int     outSplitNum;
	string  concWorkPath;
	string  workPath;
	string  workPrefix;
	string  globFnameMatch;
	string  loginUser;
	string  loginPassword;
	
public:	
  void operator = (const ZkOutput& right);

  friend bool operator == (const ZkOutput &left,const ZkOutput& right);

	friend bool operator < (const ZkOutput &left,const ZkOutput& right);

  friend std::ostream& operator<<(std::ostream& os,const ZkOutput& po);

private:
 	int getOutType(int splitNum);
 	
 	bool isPutPath();

public:
	void instancePath();
	
	string getPutRemotePath();
	
	void setPutRemotePath(string remotePath);
	
	string getFileType();
	
	void setFileType(string fileType);
	
	string getDirId();
	
	void setDirId(string dirId);
	
	string getHostOrIp();
	
	void setHostOrIp(string hostOrIp);
	
	int getPort();
	
	void setPort(int port);
	
	string getPath();
		
	void setPath(string path);
	
	string getWorkPathFromPath();
	
	int getServType();

	void setServType(int servType);
	
	int getOutType();
	
	void setOutType(int outType);
	
	int getGetOrPut();
	
	void setGetOrPut(int getOrPut);
	
	int getOutSplitNum();
	
	void setOutSplitNum(int outSplitNum);
	
	string getConcWorkPath();
	
	void setConcWorkPath(string concWorkPath);
	
	string getWorkPath();
	
	void setWorkPath(string workPath);
	
	string getWorkPrefix();
	
	void setWorkPrefix(string workPrefix);
	
	string getGlobFnameMatch();
	
	void setGlobFnameMatch(string globFnameMatch);
	
	string getLoginUser();
	
	void setLoginUser(string loginUser);
	
	string getLoginPassword();
	
	void setLoginPassword(string loginPassword);
	
};

#endif /* ZK_OUTPUT_H_ */