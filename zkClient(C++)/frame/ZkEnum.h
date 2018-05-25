#ifndef ZkEnum_H_
#define ZkEnum_H_

#include <string>

extern const int SSFTP_GET_FOR_FTP  ;
extern const int SSFTP_PUT_FOR_FTP  ;
extern const int SSFTP_GET_FOR_SFTP ;
extern const int SSFTP_PUT_FOR_SFTP ;

extern const int OUT_TYPE_NORMAL_DIR;
extern const int OUT_TYPE_SUB_DIR 	;

extern const std::string MODULE_NAME_EDIRINFOSYNC 		;
extern const std::string MODULE_NAME_SSFTPGET 				;
extern const std::string MODULE_NAME_SSFTPPUT 				;
extern const std::string MODULE_NAME_ROAMDEAL 				;
extern const std::string MODULE_NAME_RR 							;
extern const std::string MODULE_NAME_FSEND 						;
extern const std::string MODULE_NAME_FRECEIVE 				;
extern const std::string MODULE_NAME_GENHIGH 					;
extern const std::string MODULE_NAME_RESOURCEMANAGER 	;
extern const std::string MODULE_NAME_UNKNOWN 					;

extern const std::string ROOT_TYPE_REGISTER				  	;
extern const std::string ROOT_TYPE_TASKSTATE				  ;
extern const std::string ROOT_TYPE_RESOURCEMANAGER	  ;

extern const std::string VALUE_TYPE_OUTPUT 						;
extern const std::string VALUE_TYPE_ERROUT 						;
extern const std::string VALUE_TYPE_OUTPUT_TASKSTATE	;
extern const std::string VALUE_TYPE_ERROUT_TASKSTATE	;

extern const std::string PUT_PATH_SEP 			 ;
extern const std::string ZK_PATH_SEP 				 ;
extern const std::string ZK_LINK_LETTER      ;
extern const std::string ZK_ROOT_PATH        ; // zk root path
extern const std::string SHARE_LOCK          ; // 分布式锁路径
extern const int   connectionTimeoutMs ; // 默认 zk 超时时长
extern const long  defaultTimeToWait   ;

extern const std::string ZK_ISACTIVE;
extern const std::string ZK_ASSIGN_INPUT_LIST;

#endif /* ZkEnum_H_ */