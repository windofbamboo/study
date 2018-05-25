#ifndef JSON_UTIL_H_
#define JSON_UTIL_H_

#include <vector>
#include "ZkOutput.h"
#include "ZkActiveValue.h"

#include "stringbuffer.h"
#include "writer.h"
#include "document.h"

using namespace rapidjson;
using namespace std;

extern bool Json2ZkOutput(const string json,ZkOutput &zkOutput);

extern bool Json2ZkOutputList(const string json,vector<ZkOutput> &zkOutputVec);

extern bool Json2ZkActiveValue(const string json,ZkActiveValue &zkActiveValue);

extern string ZkOutput2Json(ZkOutput zkOutput);

extern string ZkOutputList2Json(vector<ZkOutput> zkOutputVec);

extern string ZkActiveValue2Json(ZkActiveValue zkActiveValue);

#endif 