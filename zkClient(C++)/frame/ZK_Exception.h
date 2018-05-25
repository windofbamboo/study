// Exception.h: interface for the Exception class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(ZK_EXCEPTION_H_)
#define ZK_EXCEPTION_H_
#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <string>
#include <map>
#include <vector>
#include <string>

#ifdef WIN32
#pragma warning(disable:4786)
#else
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/stat.h>
#endif
using namespace std;
//??�쳣��Ϣ��

namespace ZK
{
	class ExceptionMsgMSG
	{
	public:
		//??�쳣�������ļ�
		char m_filename[255];
		//??�쳣����������
		int  m_linenum;
		//??�쳣��Ϣ
		char m_msg[1024];	
		
		//??���캯��
		//??������
		//??-filename �ļ���
		//??-linenum �ļ��к�
		//??-format ��¼��ʽ
		//??-...  ��������
		//??����ֵ��
		//??1 �ɹ�
		//??0 ʧ��
		ExceptionMsgMSG(const char * filename,const int linenum ,const char * format, ...);
		
		ExceptionMsgMSG(const int linenum,const char * filename, const char * msg)
		{
			strcpy(m_filename,filename);
			m_linenum = linenum;
			strncpy(m_msg,msg,sizeof(m_msg)-20);
			if (strlen(msg) > sizeof(m_msg)-20)
			{
				strcat(m_msg,"[[msg����!]]");
			}
		};
	};
	//#define THROW throw Exception(__FILE__,__LINE__,
	
	//??�쳣������
	class ZK_Exception :public std::exception
	{
	public:
		//??��Ϣ��ӡ
		//??������
		//??��
		//??����ֵ��
		//??1 �ɹ�
		//??0 ʧ��
		void toString();
		
		//??��Ϣת�����ַ���
		//??������
		//??��
		//??����ֵ��
		//??������Ϣ
		char *GetString();
		
		//??���캯��
		//??������
		//??-filename �ļ���
		//??-linenum �ļ��к�
		//??-format ��¼��ʽ
		//??-...  ��������
		//??����ֵ��
		//??��
		ZK_Exception(const char * filename,const int linenum ,const int iErrorCode,const char * format, ...);
		
		//??�����쳣��Ϣ
		//??������
		//??-msg ������Ϣ����
		//??����ֵ��
		//??��
		void Push(ExceptionMsgMSG msg);
		
		//??�쳣��Ϣvector����
		vector<ExceptionMsgMSG> m_msg;
		
		//??�쳣��Ϣ
		char m_errstr[4*1024];

		int getErrCode();
	
		//??��������
		virtual ~ZK_Exception() throw(){}

	private:
		int m_iErrCode;
	
	};
};

#endif
