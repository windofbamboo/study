//#include <stdio.h>
//#include <unistd.h>

#include <string>
#include <map>
#include <sstream>

extern char **environ;

using namespace std;

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





int main(int argc, char *argv[])
{
	 string path="${WORK_HOME}/src/roam";
   map<string,string> envMap = getEnv();
   
   string newPath=getStringByEnvVarName(path,envMap);
   
   return 0;
}