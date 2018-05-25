#ifndef ZK_FILE_TYPE_VALUE_H_
#define ZK_FILE_TYPE_VALUE_H_

#include <string>
#include <vector>
#include "ZkOutput.h"

using namespace std;

class ZkFileTypeValue {
	
private:
	string fileType;
	vector<ZkOutput> zkOutputs;
	vector<ZkOutput> zkErrOuts;
	
public:
	ZkFileTypeValue();
  ZkFileTypeValue(const ZkFileTypeValue& right);

  ~ZkFileTypeValue();		
	
public:
	
	void operator = (const ZkFileTypeValue& right);

  friend bool operator == (const ZkFileTypeValue &left,const ZkFileTypeValue& right);

	friend bool operator < (const ZkFileTypeValue &left,const ZkFileTypeValue& right);

  friend std::ostream& operator<<(std::ostream& os,const ZkFileTypeValue& po);
	
public:
		
	string getFileType();

	void setFileType(string fileType);
	
	vector<ZkOutput> getZkOutputs();
	
	void setZkOutputs(vector<ZkOutput> zkOutputs );
	
	vector<ZkOutput> getZkErrOuts();
	
	void setZkErrOuts(vector<ZkOutput> zkErrOuts );
};

#endif /* ZK_FILE_TYPE_VALUE_H_ */
