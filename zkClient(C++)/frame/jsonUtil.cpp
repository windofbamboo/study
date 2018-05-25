#include "jsonUtil.h"

bool Json2ZkOutput(const string json,ZkOutput &zkOutput)
{
	rapidjson::Document newDoc;
	newDoc.Parse(json.c_str()); //将读取的内容转换为dom元素
	
	if (newDoc.HasParseError()) {
		rapidjson::ParseErrorCode errcode = newDoc.GetParseError();
		#ifdef _DEBUG_
    	cout<<"Parse error, errcode:"<<errcode<<" "<<__FILE__<<__LINE__<<endl;
    #endif
  	return false;
	}
	else{
    	if(newDoc.HasMember("fileType")){
    		if(newDoc["fileType"].IsString()){
    			zkOutput.setFileType(newDoc["fileType"].GetString());
    		}
    	}
    	
    	if(newDoc.HasMember("hostOrIp")){
    		if(newDoc["hostOrIp"].IsString()){
    			zkOutput.setHostOrIp(newDoc["hostOrIp"].GetString());
    		}
    	}
    	
    	if(newDoc.HasMember("dirId")){
    		if(newDoc["dirId"].IsString()){
    			zkOutput.setDirId(newDoc["dirId"].GetString());
    		}
    	}
    	
			if(newDoc.HasMember("path")){
    		if(newDoc["path"].IsString()){
    			zkOutput.setPath(newDoc["path"].GetString());
    		}
    	}
			
			if(newDoc.HasMember("servType")){
    		if(newDoc["servType"].IsInt()){
    			zkOutput.setServType(newDoc["servType"].GetInt());
    		}
    	}

			if(newDoc.HasMember("outType")){
    		if(newDoc["outType"].IsInt()){
    			zkOutput.setOutType(newDoc["outType"].GetInt());
    		}
    	}
    	
    	if(newDoc.HasMember("getOrPut")){
    		if(newDoc["getOrPut"].IsInt()){
    			zkOutput.setGetOrPut(newDoc["getOrPut"].GetInt());
    		}
    	}
    	
    	if(newDoc.HasMember("outSplitNum")){
    		if(newDoc["outSplitNum"].IsInt()){
    			zkOutput.setOutSplitNum(newDoc["outSplitNum"].GetInt());
    		}
    	}
    	
    	if(newDoc.HasMember("concWorkPath")){
    		if(newDoc["concWorkPath"].IsString()){
    			zkOutput.setConcWorkPath(newDoc["concWorkPath"].GetString());
    		}
    	}
    	
    	if(newDoc.HasMember("workPath")){
    		if(newDoc["workPath"].IsString()){
    			zkOutput.setWorkPath(newDoc["workPath"].GetString());
    		}
    	}
    	
			if(newDoc.HasMember("workPrefix")){
    		if(newDoc["workPrefix"].IsString()){
    			zkOutput.setWorkPrefix(newDoc["workPrefix"].GetString());
    		}
    	}
    	
    	if(newDoc.HasMember("loginUser")){
    		if(newDoc["loginUser"].IsString()){
    			zkOutput.setLoginUser(newDoc["loginUser"].GetString());
    		}
    	}
    	
    	if(newDoc.HasMember("loginPassword")){
    		if(newDoc["loginPassword"].IsString()){
    			zkOutput.setLoginPassword(newDoc["loginPassword"].GetString());
    		}
    	}
    	
    	return true;
	}
}
	
bool Json2ZkOutputList(const string json,vector<ZkOutput> &zkOutputVec)
{	
	rapidjson::Document newDoc;
	newDoc.Parse(json.c_str()); //将读取的内容转换为dom元素

	if (!newDoc.HasParseError()) {
			for(rapidjson::Value::ConstValueIterator itr = newDoc.Begin(); itr != newDoc.End(); ++itr)
			{
					const Value& obj = *itr;
					if(obj.IsObject()){
							ZkOutput zkOutput;
							
			      	if(obj.HasMember("fileType")){
				    		if(obj["fileType"].IsString()){
				    			zkOutput.setFileType(obj["fileType"].GetString());
				    		}
				    	}
				    	
				    	if(obj.HasMember("hostOrIp")){
				    		if(obj["hostOrIp"].IsString()){
				    			zkOutput.setHostOrIp(obj["hostOrIp"].GetString());
				    		}
				    	}
				    	
				    	if(obj.HasMember("dirId")){
				    		if(obj["dirId"].IsString()){
				    			zkOutput.setDirId(obj["dirId"].GetString());
				    		}
				    	}
				    	
							if(obj.HasMember("path")){
				    		if(obj["path"].IsString()){
				    			zkOutput.setPath(obj["path"].GetString());
				    		}
				    	}
							
							if(obj.HasMember("servType")){
				    		if(obj["servType"].IsInt()){
				    			zkOutput.setServType(obj["servType"].GetInt());
				    		}
				    	}
				
							if(obj.HasMember("outType")){
				    		if(obj["outType"].IsInt()){
				    			zkOutput.setOutType(obj["outType"].GetInt());
				    		}
				    	}
				    	
				    	if(obj.HasMember("getOrPut")){
				    		if(obj["getOrPut"].IsInt()){
				    			zkOutput.setGetOrPut(obj["getOrPut"].GetInt());
				    		}
				    	}
				    	
				    	if(obj.HasMember("outSplitNum")){
				    		if(obj["outSplitNum"].IsInt()){
				    			zkOutput.setOutSplitNum(obj["outSplitNum"].GetInt());
				    		}
				    	}
				    	
				    	if(obj.HasMember("concWorkPath")){
				    		if(obj["concWorkPath"].IsString()){
				    			zkOutput.setConcWorkPath(obj["concWorkPath"].GetString());
				    		}
				    	}
				    	
				    	if(obj.HasMember("workPath")){
				    		if(obj["workPath"].IsString()){
				    			zkOutput.setWorkPath(obj["workPath"].GetString());
				    		}
				    	}
				    	
							if(obj.HasMember("workPrefix")){
				    		if(obj["workPrefix"].IsString()){
				    			zkOutput.setWorkPrefix(obj["workPrefix"].GetString());
				    		}
				    	}
				    	
				    	if(obj.HasMember("loginUser")){
				    		if(obj["loginUser"].IsString()){
				    			zkOutput.setLoginUser(obj["loginUser"].GetString());
				    		}
				    	}
				    	
				    	if(obj.HasMember("loginPassword")){
				    		if(obj["loginPassword"].IsString()){
				    			zkOutput.setLoginPassword(obj["loginPassword"].GetString());
				    		}
				    	}
							
							zkOutputVec.push_back(zkOutput);
					}
			}
	}else{
		rapidjson::ParseErrorCode errcode = newDoc.GetParseError();
		#ifdef _DEBUG_
    	cout<<"Parse error, errcode:"<<errcode<<" "<<__FILE__<<__LINE__<<endl;
    #endif
		return false;
	}
	return true;
}


bool Json2ZkActiveValue(const string json,ZkActiveValue &zkActiveValue)
{
	rapidjson::Document newDoc;
	newDoc.Parse(json.c_str());
	
	if (newDoc.HasParseError()) {
		rapidjson::ParseErrorCode errcode = newDoc.GetParseError();
		#ifdef _DEBUG_
    	cout<<"Parse error, errcode:"<<errcode<<" "<<__FILE__<<__LINE__<<endl;
    #endif
  	return false;
	}else{
			if(newDoc.HasMember("procId")){
    		if(newDoc["procId"].IsString()){
    			zkActiveValue.setProcId(newDoc["procId"].GetString());
    		}
    	}
			
			if(newDoc.HasMember("hostOrIp")){
    		if(newDoc["hostOrIp"].IsString()){
    			zkActiveValue.setHostOrIp(newDoc["hostOrIp"].GetString());
    		}
    	}
    	
    	if(newDoc.HasMember("channelId")){
    		if(newDoc["channelId"].IsString()){
    			zkActiveValue.setChannelId(newDoc["channelId"].GetString());
    		}
    	}
    	return true;
	}
}


string ZkOutput2Json(ZkOutput zkOutput)
{
	StringBuffer s;
  Writer<StringBuffer> writer(s);
	
	writer.StartObject();
	writer.Key("fileType");
	writer.String(zkOutput.getFileType().c_str());
	writer.Key("hostOrIp");
	writer.String(zkOutput.getHostOrIp().c_str());
	writer.Key("port");
	writer.Int(zkOutput.getPort());
	writer.Key("dirId");
	writer.String(zkOutput.getDirId().c_str());
	writer.Key("path");
	writer.String(zkOutput.getPath().c_str());
	writer.Key("servType");
	writer.Int(zkOutput.getServType());
	writer.Key("outType");
	writer.Int(zkOutput.getOutType());
	writer.Key("getOrPut");
	writer.Int(zkOutput.getGetOrPut());
	writer.Key("outSplitNum");
	writer.Int(zkOutput.getOutSplitNum());
	writer.Key("concWorkPath");
	writer.String(zkOutput.getConcWorkPath().c_str());
	writer.Key("workPath");
	writer.String(zkOutput.getWorkPath().c_str());
	writer.Key("workPrefix");
	writer.String(zkOutput.getWorkPrefix().c_str());
	writer.Key("globFnameMatch");
	writer.String(zkOutput.getGlobFnameMatch().c_str());
	writer.Key("loginUser");
	writer.String(zkOutput.getLoginUser().c_str());
	writer.Key("loginPassword");
	writer.String(zkOutput.getLoginPassword().c_str());
  writer.EndObject();
  
  return s.GetString();
}

string ZkOutputList2Json(vector<ZkOutput> zkOutputVec)
{
	StringBuffer s;
  Writer<StringBuffer> writer(s);
	
	writer.StartArray();
	for(vector<ZkOutput>::iterator itr =zkOutputVec.begin();
																 itr!=zkOutputVec.end();itr++)
	{
		writer.StartObject();
		writer.Key("fileType");
		writer.String(itr->getFileType().c_str());
		writer.Key("hostOrIp");
		writer.String(itr->getHostOrIp().c_str());
		writer.Key("port");
		writer.Int(itr->getPort());
		writer.Key("dirId");
		writer.String(itr->getDirId().c_str());
		writer.Key("path");
		writer.String(itr->getPath().c_str());
		writer.Key("servType");
		writer.Int(itr->getServType());
		writer.Key("outType");
		writer.Int(itr->getOutType());
		writer.Key("getOrPut");
		writer.Int(itr->getGetOrPut());
		writer.Key("outSplitNum");
		writer.Int(itr->getOutSplitNum());
		writer.Key("concWorkPath");
		writer.String(itr->getConcWorkPath().c_str());
		writer.Key("workPath");
		writer.String(itr->getWorkPath().c_str());
		writer.Key("workPrefix");
		writer.String(itr->getWorkPrefix().c_str());
		writer.Key("globFnameMatch");
		writer.String(itr->getGlobFnameMatch().c_str());
		writer.Key("loginUser");
		writer.String(itr->getLoginUser().c_str());
		writer.Key("loginPassword");
		writer.String(itr->getLoginPassword().c_str());
	  writer.EndObject();
	}
	writer.EndArray();
  
  return s.GetString();	
}

string ZkActiveValue2Json(ZkActiveValue zkActiveValue)
{
	StringBuffer s;
  Writer<StringBuffer> writer(s);
	
	writer.StartObject();
	writer.Key("hostOrIp");
	writer.String(zkActiveValue.getHostOrIp().c_str());
	writer.Key("procId");
	writer.String(zkActiveValue.getProcId().c_str());
	writer.Key("channelId");
	writer.String(zkActiveValue.getChannelId().c_str());
  writer.EndObject();
	
	return s.GetString();
}
