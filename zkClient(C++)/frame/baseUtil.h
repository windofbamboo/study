#ifndef BASE_UTIL_H_
#define BASE_UTIL_H_

#include <string>
#include <map>
#include <vector>

using namespace std;

extern char **environ;

bool isLetterOrDigit(char c);

map<string,string> getEnv();

string getStringByEnvVarName(string path,map<string, string> envMap);

extern string getTruePathByEnv(string path);

extern bool endsWith(string fullStr,string endStr);

extern bool startsWith(string fullStr,string startStr);

extern vector<string> tokenize_path(string path,string pathStep);

extern string toks_to_path(vector<string>,string pathStep);

extern vector<string> toks_to_allPath(vector<string> tokVec,string pathStep);

extern vector<string> stringSplit(string str,string pattern);

#endif 