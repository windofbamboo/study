#include <stdio.h>
#include <unistd.h>
#include <vector>
#include "stringbuffer.h"
#include "writer.h"
#include "document.h"

#include <netdb.h>
#include <sys/socket.h>
#include <arpa/inet.h>

using namespace rapidjson;
using namespace std;

struct TestValue {
	TestValue(const char* hostOrIp,const long procId,const char* channelId){
		this->hostOrIp = const_cast<char*>(hostOrIp);
		this->procId = procId;
		this->channelId = const_cast<char*>(channelId);
	};
	TestValue(){};

	void setHostOrIp(const char* hostOrIp){
		this->hostOrIp = const_cast<char*>(hostOrIp);
	}
	void setProcId(const long procId){
		this->procId = procId;
	}
	void setChannelId(const char* channelId){
		this->channelId = const_cast<char*>(channelId);
	}

	char* hostOrIp;
	long procId;
	char* channelId;
};

bool Json2TestValue(const char* json,TestValue &testValue){

		rapidjson::Document newDoc;
		newDoc.Parse(json); //将读取的内容转换为dom元素
		
		bool hasErr = false;
		if (newDoc.HasParseError()) {
    	hasErr = true;
		}else{

      	if (newDoc.HasMember("hostOrIp")) {
      		
      		 assert(newDoc["hostOrIp"].IsString());	
   				 printf("hostOrIp = %s\n", newDoc["hostOrIp"].GetString());
   				 
      		 char* value;
      		 int len = strlen(newDoc["hostOrIp"].GetString());
      		 value=(char *)malloc(len);
      		 snprintf(value, len, "%s", newDoc["hostOrIp"].GetString());
   						
   				 testValue.setHostOrIp(value);   		
           //testValue.hostOrIp =newDoc["hostOrIp"].GetString();
      	}else{
      		hasErr = true;
      	}
      	if (newDoc.HasMember("procId")) {
           testValue.procId = newDoc["procId"].GetInt();
      	}else{
      		hasErr = true;
      	}
      	if (newDoc.HasMember("channelId")) {
           testValue.channelId =const_cast<char*> (((string)newDoc["channelId"].GetString()).c_str());
      	}else{
      		hasErr = true;
      	}
      	if (!hasErr)
      		return true;
		}
		return false;
}

bool Json2TestValueList(const char* json,std::vector<TestValue> &testValueVec){
	
		rapidjson::Document newDoc;
		newDoc.Parse(json); //将读取的内容转换为dom元素

		if (!newDoc.HasParseError()) {
				for(rapidjson::Value::ConstValueIterator itr = newDoc.Begin(); itr != newDoc.End(); ++itr)
				{
						const Value& obj = *itr;
						if(obj.IsObject()){
								TestValue testValue;
								if (obj.HasMember("hostOrIp")) {
								   printf("hostOrIp = %s\n", obj["hostOrIp"].GetString());
								   //testValue.hostOrIp = const_cast<char*> (obj["hostOrIp"].GetString());
								}else{continue;}
								if (obj.HasMember("procId")) {
									printf("procId = %d\n", obj["procId"].GetInt());
								   //testValue.procId = obj["procId"].GetInt();
								}else{continue;}
								if (obj.HasMember("channelId")) {
									 printf("channelId = %s\n", obj["channelId"].GetString());
								   //testValue.channelId = const_cast<char*> (obj["channelId"].GetString());
								}else{continue;}
								
								testValueVec.push_back(testValue);
						}
				}
		}
		return true;
}


bool TestValue2Json(const TestValue testValue,char* &json)
{
	StringBuffer s;
  Writer<StringBuffer> writer(s);
	
	writer.StartObject();
	writer.Key("hostOrIp");
	writer.String(testValue.hostOrIp);
	writer.Key("procId");
	writer.Int(testValue.procId);
	writer.Key("channelId");
	writer.String(testValue.channelId);
  writer.EndObject();
  
  int len = strlen(s.GetString()) +1;

  json=(char *)malloc(len);
	snprintf(json, len, "%s", s.GetString());
}

bool TestValueList2Json(const std::vector<TestValue> testValueVec,char* &json)
{	
	StringBuffer s;
  Writer<StringBuffer> writer(s);
	
	writer.StartArray();
	for(std::vector<TestValue>::const_iterator itr =testValueVec.begin();
																			 	itr!=testValueVec.end();itr++)
	{
		writer.StartObject();
		writer.Key("hostOrIp");
		writer.String(itr->hostOrIp);
		writer.Key("procId");
		writer.Int(itr->procId);
		writer.Key("channelId");
		writer.String(itr->channelId);
	  writer.EndObject();
	}
	writer.EndArray();
  int len = strlen(s.GetString())+1;

  json=(char *)malloc(len);
	snprintf(json, len, "%s", s.GetString());
}


int main(int argc, char *argv[]){
	
	//char* json = "{\"hostOrIp\":\"roam-compile-app\",\"procId\":23902,\"channelId\":\"101\"}";
	char* json ="{\n\t\"concWorkPath\":\"a\",\n\t\"dirId\":\"gsm-test01\",\n\t\"fileType\":\"gsm-type\",\n\t\"outSplitNum\":3,\n\t\"outType\":\"OUT_TYPE_SUB_DIR\",\n\t\"path\":\"/a/b/c\",\n\t\"servType\":0\n}";
	
	int len=strlen(json)+1;
	char* copy=(char *)malloc(len);
	snprintf(copy, len, "%s", json);
	
	if(copy==json){
		printf(" 1 \n");
	}
	
	if(*copy==*json){
		printf(" 2 \n");
	}
	
	rapidjson::Document newDoc;
	newDoc.Parse(json);
		
		if (newDoc.HasParseError()) {
			
			ParseErrorCode errcode = newDoc.GetParseError();
			
    	printf(" err !\n");
		}else{
			printf(" sucess !\n");
		}
}

/*
int main(int argc, char *argv[]) {
		
	long procId    = (long) getpid();
	
	TestValue* testValue1 = new TestValue("127.0.0.1:2181",110000,"p1");
	TestValue* testValue2 = new TestValue("127.0.0.1:2182",110001,"p2");
	
	std::vector<TestValue> testValueVec;
	testValueVec.push_back(*testValue1);
	testValueVec.push_back(*testValue2);
	
	char* json1;
	char* json2;
	char* json3;
	TestValue2Json(*testValue1,json1);
	TestValue2Json(*testValue2,json2);
	TestValueList2Json(testValueVec,json3);
	
	//printf("json1 = %s\n", json1);
	//printf("json2 = %s\n", json2);
	//printf("json3 = %s\n", json3);
	
	//TestValue* testValue11;
	//TestValue* testValue12;
	//Json2TestValue(json1,*testValue11);
	//Json2TestValue(json2,*testValue12);
	
	vector<TestValue> testValueVec2;
	Json2TestValueList(json3,testValueVec2);
	
	return 0;
}
*/