// MsgException.cpp: implementation of the Exception class.
//
//////////////////////////////////////////////////////////////////////
#include "ZK_Exception.h"

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
using namespace ZK;

ZK_Exception::ZK_Exception(const char * filename,const int linenum ,const  int iErrorCode,const char * format ,...)
{
	char *ls_str = new char[1024 * 10];
	va_list  pvar;
	va_start (pvar, format);
	vsprintf(ls_str,format,pvar);
	va_end(pvar);
	//ExceptionMsgMSG e(filename,linenum,"%s",ls_str);
	ExceptionMsgMSG e(linenum,filename,ls_str);
	m_msg.push_back(e);
	delete ls_str;

	m_iErrCode = iErrorCode;
}

void ZK_Exception::toString()
{
#ifdef _DEBUG_	
	vector<ExceptionMsgMSG>::iterator it;
	for ( it = m_msg.begin(); it != m_msg.end(); it++)
		printf("File[%12s:%4d] Msg[ %s ]\n",it->m_filename,it->m_linenum,it->m_msg);
#endif
}

void ZK_Exception::Push(ExceptionMsgMSG msg)
{
	m_msg.push_back(msg);
}

char* ZK_Exception::GetString()
{
	vector<ExceptionMsgMSG>::iterator it;
	char *pstr = new char[1024];
	int curpos = 0;
	int leftsize = sizeof(m_errstr) -1;
	int length = 0;
	for ( it = m_msg.begin(); it != m_msg.end(); it++)
	{
		sprintf(pstr,"File[%12s:%4d] Msg[ %s ] ",it->m_filename,it->m_linenum,it->m_msg);
		length = strlen(pstr);
		leftsize -= length;
		if (leftsize < 0)
			break;
		strcpy(&m_errstr[curpos], pstr);
		curpos += length;
	}
	delete pstr;
	return m_errstr;
}

ExceptionMsgMSG::ExceptionMsgMSG(const char * filename,const int linenum ,const char * format, ...)
{
	strcpy(m_filename,filename);
	m_linenum = linenum;
	char *msg = new char[1024*2];
	va_list  pvar;
	va_start (pvar,format);
	vsprintf(msg,format,pvar);
	va_end (pvar);
	strncpy(m_msg,msg,sizeof(m_msg)-20);
	if (strlen(msg) > sizeof(m_msg)-20)
	{
		strcat(m_msg,"[[msg³¬³¤]]");
	}
	delete msg;
}

int ZK_Exception::getErrCode()
{
	return m_iErrCode;
}
