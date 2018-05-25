#include <sstream>
#include <string.h>
#include "baseUtil.h"

bool isLetterOrDigit(char c)
{
	return ((c >='0' && c<='9') || (c >='a' && c<='z') || (c >='A' && c<='Z'));
}

map<string,string> getEnv()
{
	map<string,string> envMap;
	envMap.clear();
	
	string pos="=";
	
  char **p = environ;
  while (*p != NULL)
  {
     string env = *p;
     string::size_type idx;
     idx=env.find(pos);
     
     if(idx != string::npos ){
     	string name= env.substr(0,idx);
     	string value= env.substr(idx+1);
     	envMap.insert(map<string,string>::value_type(name,value));
     }
     *p++;
  }
  return envMap;
}

string getStringByEnvVarName(string path,map<string, string> envMap) 
{
	int len = path.length();
	stringstream newPath;
	stringstream varName;
	char c;
	
	for(int i=0;i<len;i++) {
		c=path[i];
		if(c!='$') {
			newPath<<c;
			continue;
		}
		//����$
		i++;
		if(i>=len) {
			newPath<<c;
			break;
		}
		//����{
		c=path[i];
		i++;
		if(i>=len) {
			newPath<<'$';
			newPath<<c;
			break;
		}
		if(c=='{') {//��{}
			varName.str("");
			for(;i<len;i++) {
				if(path[i]=='}') {
					break;
				}
				varName<<path[i];
			}
			if(i>=len) {//������},ԭ�����
				newPath<<c;
				newPath<<varName.str();
				continue;
			}
			if(varName.str().length()==0) {//�Ǹ�${},ԭ�����
				newPath<<c;
				newPath<<path[i];
				continue;
			}

			map<string, string>::iterator itr = envMap.find(varName.str());
			if(itr!=envMap.end()){
				newPath<<itr->second;
			}else{
				newPath<<"${"<<varName<<"}";
			}
			continue;
		}
		//������{}�ı���
		if(!(isLetterOrDigit(c)||c=='_')){//�ǲ��Ǳ���������
			newPath<<'$';
			newPath<<c;
			i--;
			continue;
		}
		varName.str("");
		varName<<c;
		for(;i<len;i++) {
			if(!(isLetterOrDigit(path[i])||path[i]=='_')){//�ǲ��Ǳ���������
				i--;
				break;
			}
			varName<<path[i];
		}

		map<string, string>::iterator itr = envMap.find(varName.str());
		if(itr!=envMap.end()){
			newPath<<itr->second;
		}else{
			newPath<<"${"<<varName<<"}";
		}
	}
	return newPath.str();
}

string getTruePathByEnv(string path)
{
   map<string,string> envMap = getEnv();
   return getStringByEnvVarName(path,envMap);
}


bool endsWith(string fullStr,string endStr)
{
	int len1 = fullStr.length();
	int len2 = endStr.length();
	
	if(len1<len2)
		return false;

	int len3=	len1 - len2;
	return (fullStr.substr(len3,len2) == endStr);
}

bool startsWith(string fullStr,string startStr)
{
	int len1 = fullStr.length();
	int len2 = startStr.length();
	
	if(len1<len2)
		return false;
		
	string str = fullStr.substr(0,len2);

	return (str == startStr);
}

vector<string> tokenize_path(string path,string pathStep)
{
	char * strc = new char[strlen(path.c_str())+1];
  strcpy(strc, path.c_str());
  vector<string> resultVec;
  char *ptr;
  char *p;
  ptr = strtok_r(strc, pathStep.c_str(), &p);
  while(ptr != NULL){
      resultVec.push_back(std::string(ptr));
      ptr = strtok_r(NULL, pathStep.c_str(), &p);
  }
  
  delete[] strc;
  return resultVec;
}

string toks_to_path(vector<string> tokVec,string pathStep)
{
	stringstream tempPath;
	tempPath<<pathStep;
	
  int size = tokVec.size();
  for (int i = 0; i < size; i++) {
      tempPath<<tokVec[i];
      if (i < (size - 1)) {
          tempPath<<pathStep;
      }
  }
  return tempPath.str();
}

vector<string> toks_to_allPath(vector<string> tokVec,string pathStep)
{
	vector<string> resultVec;
	
	stringstream tempPath;
	tempPath<<pathStep;
	
  int size = tokVec.size();
  for (int i = 0; i < size; i++) {
      tempPath<<tokVec[i];
      resultVec.push_back(tempPath.str());
      if (i < (size - 1)) {
          tempPath<<pathStep;
      }
  }
  return resultVec;
}



vector<string> stringSplit(string str,string pattern)
{
    char * strc = new char[strlen(str.c_str())+1];
    strcpy(strc, str.c_str());
    vector<string> resultVec;
    char *ptr;
    char *p;
    ptr = strtok_r(strc, pattern.c_str(), &p);
    while(ptr != NULL){
        resultVec.push_back(std::string(ptr));
        ptr = strtok_r(NULL, pattern.c_str(), &p);
    }
    
    delete[] strc;
    return resultVec;
}