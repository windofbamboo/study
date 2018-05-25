#include"ExceptionErrCode.h"

const char* ExceptionErrCode::EXCEPTION_MSG[];
#define DeclareOneExceptionCodeMsg(code,msg) EXCEPTION_MSG[code-10000]=msg;

const int ExceptionErrCode::ERRCODE_MIN;
const int ExceptionErrCode::ERRCODE_MAX;

const int ExceptionErrCode::UNKNOWN;

const int ExceptionErrCode::E_ZK_ZSYSTEMERROR;
const int ExceptionErrCode::E_ZK_ZRUNTIMEINCONSISTENCY;
const int ExceptionErrCode::E_ZK_ZDATAINCONSISTENCY;
const int ExceptionErrCode::E_ZK_ZCONNECTIONLOSS;
const int ExceptionErrCode::E_ZK_ZMARSHALLINGERROR;
const int ExceptionErrCode::E_ZK_ZUNIMPLEMENTED;
const int ExceptionErrCode::E_ZK_ZOPERATIONTIMEOUT;
const int ExceptionErrCode::E_ZK_ZBADARGUMENTS;
const int ExceptionErrCode::E_ZK_ZINVALIDSTATE;

const int ExceptionErrCode::E_ZK_ZAPIERROR;
const int ExceptionErrCode::E_ZK_ZNONODE;
const int ExceptionErrCode::E_ZK_ZNOAUTH;
const int ExceptionErrCode::E_ZK_ZBADVERSION;
const int ExceptionErrCode::E_ZK_ZNOCHILDRENFOREPHEMERALS;
const int ExceptionErrCode::E_ZK_ZNODEEXISTS;

const int ExceptionErrCode::E_ZK_ZNOTEMPTY;
const int ExceptionErrCode::E_ZK_ZSESSIONEXPIRED;
const int ExceptionErrCode::E_ZK_ZINVALIDCALLBACK;
const int ExceptionErrCode::E_ZK_ZINVALIDACL;
const int ExceptionErrCode::E_ZK_ZAUTHFAILED;
const int ExceptionErrCode::E_ZK_ZCLOSING;
const int ExceptionErrCode::E_ZK_ZNOTHING;
const int ExceptionErrCode::E_ZK_ZSESSIONMOVED;
	
	
const int ExceptionErrCode::E_FRAME_BASE;
	
const int ExceptionErrCode::E_GET_HOSTNAME;

void ExceptionErrCode::Initialize()
{
	DeclareOneExceptionCodeMsg(UNKNOWN,								 				" unKnown ")
	
	DeclareOneExceptionCodeMsg(E_ZK_ZSYSTEMERROR,							" System and server-side errors ")
	DeclareOneExceptionCodeMsg(E_ZK_ZRUNTIMEINCONSISTENCY,		" A runtime inconsistency was found ")
	DeclareOneExceptionCodeMsg(E_ZK_ZDATAINCONSISTENCY,				" A data inconsistency was found ")
	DeclareOneExceptionCodeMsg(E_ZK_ZCONNECTIONLOSS,					" Connection to the server has been lost ")
	DeclareOneExceptionCodeMsg(E_ZK_ZMARSHALLINGERROR,				" Error while marshalling or unmarshalling data ")
	DeclareOneExceptionCodeMsg(E_ZK_ZUNIMPLEMENTED,						" Operation is unimplemented ")
	DeclareOneExceptionCodeMsg(E_ZK_ZOPERATIONTIMEOUT,				" Operation timeout ")
	DeclareOneExceptionCodeMsg(E_ZK_ZBADARGUMENTS,						" Invalid arguments ")
	DeclareOneExceptionCodeMsg(E_ZK_ZINVALIDSTATE,						" Invliad zhandle state ")
	
	DeclareOneExceptionCodeMsg(E_ZK_ZAPIERROR,								" API errors ")
	DeclareOneExceptionCodeMsg(E_ZK_ZNONODE,								 	" Node does not exist ")
	DeclareOneExceptionCodeMsg(E_ZK_ZNOAUTH,								 	" Not authenticated ")
	DeclareOneExceptionCodeMsg(E_ZK_ZBADVERSION,							" Version conflict ")
	DeclareOneExceptionCodeMsg(E_ZK_ZNOCHILDRENFOREPHEMERALS,	" Ephemeral nodes may not have children ")
	DeclareOneExceptionCodeMsg(E_ZK_ZNODEEXISTS,							" The node already exists ")
	
	DeclareOneExceptionCodeMsg(E_ZK_ZNOTEMPTY,								" The node has children ")
	DeclareOneExceptionCodeMsg(E_ZK_ZSESSIONEXPIRED,					" The session has been expired by the server ")
	DeclareOneExceptionCodeMsg(E_ZK_ZINVALIDCALLBACK,					" Invalid callback specified ")
	DeclareOneExceptionCodeMsg(E_ZK_ZINVALIDACL,							" Invalid ACL specified ")
	DeclareOneExceptionCodeMsg(E_ZK_ZAUTHFAILED,							" Client authentication failed ")
	DeclareOneExceptionCodeMsg(E_ZK_ZCLOSING,								 	" ZooKeeper is closing ")
	DeclareOneExceptionCodeMsg(E_ZK_ZNOTHING,								 	" (not error) no server responses to process ")
	DeclareOneExceptionCodeMsg(E_ZK_ZSESSIONMOVED,						" session moved to another server, so operation is ignored ")
	
	DeclareOneExceptionCodeMsg(E_GET_HOSTNAME,						    " get hostname err! ")
	
}

const char* ExceptionErrCode::GetMsgByCode(const   int iEc)
{
	if(iEc >= (MAX_EXCEPTION_NUM + 10000)||iEc<10000)
	{
		return EXCEPTION_MSG[ExceptionErrCode::UNKNOWN - 10000];
	}

	return EXCEPTION_MSG[iEc - 10000];
}

