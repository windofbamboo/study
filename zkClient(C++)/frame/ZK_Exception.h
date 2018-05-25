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
//??异常信息类

namespace ZK
{
	class ExceptionMsgMSG
	{
	public:
		//??异常发生的文件
		char m_filename[255];
		//??异常发生的行数
		int  m_linenum;
		//??异常信息
		char m_msg[1024];	
		
		//??构造函数
		//??参数：
		//??-filename 文件名
		//??-linenum 文件行号
		//??-format 记录格式
		//??-...  变量参数
		//??返回值：
		//??1 成功
		//??0 失败
		ExceptionMsgMSG(const char * filename,const int linenum ,const char * format, ...);
		
		ExceptionMsgMSG(const int linenum,const char * filename, const char * msg)
		{
			strcpy(m_filename,filename);
			m_linenum = linenum;
			strncpy(m_msg,msg,sizeof(m_msg)-20);
			if (strlen(msg) > sizeof(m_msg)-20)
			{
				strcat(m_msg,"[[msg超长!]]");
			}
		};
	};
	//#define THROW throw Exception(__FILE__,__LINE__,
	
	//??异常处理类
	class ZK_Exception :public std::exception
	{
	public:
		//??信息打印
		//??参数：
		//??无
		//??返回值：
		//??1 成功
		//??0 失败
		void toString();
		
		//??信息转化成字符串
		//??参数：
		//??无
		//??返回值：
		//??错误信息
		char *GetString();
		
		//??构造函数
		//??参数：
		//??-filename 文件名
		//??-linenum 文件行号
		//??-format 记录格式
		//??-...  变量参数
		//??返回值：
		//??无
		ZK_Exception(const char * filename,const int linenum ,const int iErrorCode,const char * format, ...);
		
		//??迭代异常信息
		//??参数：
		//??-msg 错误信息对象
		//??返回值：
		//??无
		void Push(ExceptionMsgMSG msg);
		
		//??异常信息vector类型
		vector<ExceptionMsgMSG> m_msg;
		
		//??异常信息
		char m_errstr[4*1024];

		int getErrCode();
	
		//??析构函数
		virtual ~ZK_Exception() throw(){}

	private:
		int m_iErrCode;
	
	};
};

#endif
