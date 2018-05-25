#ifndef ExceptionErrCode_H_
#define ExceptionErrCode_H_

#include "ZK_Exception.h"

using namespace ZK;

class ExceptionErrCode
{
public:

	const static int ERRCODE_MIN          = 10300;
	const static int ERRCODE_MAX          = 12000;

	const static int UNKNOWN 							= 10300;
	//zk操作错误
	const static int E_ZK_ZSYSTEMERROR 					= ERRCODE_MIN + 1;
	const static int E_ZK_ZRUNTIMEINCONSISTENCY = ERRCODE_MIN + 2;
  const static int E_ZK_ZDATAINCONSISTENCY 		= ERRCODE_MIN + 3;
  const static int E_ZK_ZCONNECTIONLOSS 			= ERRCODE_MIN + 4;
  const static int E_ZK_ZMARSHALLINGERROR 		= ERRCODE_MIN + 5;
  const static int E_ZK_ZUNIMPLEMENTED 				= ERRCODE_MIN + 6;
  const static int E_ZK_ZOPERATIONTIMEOUT 		= ERRCODE_MIN + 7;
  const static int E_ZK_ZBADARGUMENTS 				= ERRCODE_MIN + 8;
  const static int E_ZK_ZINVALIDSTATE 				= ERRCODE_MIN + 9;
	
	const static int E_ZK_ZAPIERROR 						= ERRCODE_MIN + 10;
	const static int E_ZK_ZNONODE 							= ERRCODE_MIN + 11;
	const static int E_ZK_ZNOAUTH 							= ERRCODE_MIN + 12;
	const static int E_ZK_ZBADVERSION 					= ERRCODE_MIN + 13;
	const static int E_ZK_ZNOCHILDRENFOREPHEMERALS	= ERRCODE_MIN + 14;
	const static int E_ZK_ZNODEEXISTS						= ERRCODE_MIN + 15;
	
	const static int E_ZK_ZNOTEMPTY							= ERRCODE_MIN + 16;
	const static int E_ZK_ZSESSIONEXPIRED				= ERRCODE_MIN + 17;
	const static int E_ZK_ZINVALIDCALLBACK			= ERRCODE_MIN + 18;
	const static int E_ZK_ZINVALIDACL						= ERRCODE_MIN + 19;
	const static int E_ZK_ZAUTHFAILED						= ERRCODE_MIN + 20;
	const static int E_ZK_ZCLOSING							= ERRCODE_MIN + 21;
	const static int E_ZK_ZNOTHING							= ERRCODE_MIN + 22;
	const static int E_ZK_ZSESSIONMOVED					= ERRCODE_MIN + 23;
	
	
	const static int E_FRAME_BASE 							= 10400;
	
	
	const static int E_GET_HOSTNAME							= E_FRAME_BASE + 1;

public:
	static void Initialize();

	/************************************************************************/
	/*
	根据异常代码返回异常信息
	本函数无异常抛出
	*/
	/************************************************************************/
	static const char * GetMsgByCode(const  int iEc);

private:
	const static int 	MAX_EXCEPTION_NUM = 2000;
	const static char* EXCEPTION_MSG[MAX_EXCEPTION_NUM];
	ExceptionErrCode();
	ExceptionErrCode(ExceptionErrCode & e);
};

#endif /* ExceptionErrCode_H_ */

