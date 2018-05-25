#include "ZkEnum.h"

const int SSFTP_GET_FOR_FTP  = 0;
const int SSFTP_PUT_FOR_FTP  = 1;
const int SSFTP_GET_FOR_SFTP = 2;
const int SSFTP_PUT_FOR_SFTP = 3;

const int OUT_TYPE_NORMAL_DIR= 0;
const int OUT_TYPE_SUB_DIR 	 = 1;

const std::string MODULE_NAME_EDIRINFOSYNC 		= "edirinfosync";
const std::string MODULE_NAME_SSFTPGET 				= "ssftpget";
const std::string MODULE_NAME_SSFTPPUT 				= "ssftpput";
const std::string MODULE_NAME_ROAMDEAL 				= "roamdeal";
const std::string MODULE_NAME_RR 							= "rr";
const std::string MODULE_NAME_FSEND 					= "fsend";
const std::string MODULE_NAME_FRECEIVE 				= "freceive";
const std::string MODULE_NAME_GENHIGH 				= "genhigh";
const std::string MODULE_NAME_RESOURCEMANAGER = "resourcemanager";
const std::string MODULE_NAME_UNKNOWN 				= "unknown";

const std::string ROOT_TYPE_REGISTER				  = "register"; 
const std::string ROOT_TYPE_TASKSTATE				  = "task_state"; 
const std::string ROOT_TYPE_RESOURCEMANAGER	  = "res_balance_manager_state";

const std::string VALUE_TYPE_OUTPUT 					= "output";
const std::string VALUE_TYPE_ERROUT 					= "errout"; 
const std::string VALUE_TYPE_OUTPUT_TASKSTATE	= "output_state";
const std::string VALUE_TYPE_ERROUT_TASKSTATE	= "errout_state";


const std::string PUT_PATH_SEP 				= ";";
const std::string ZK_PATH_SEP 				= "/";
const std::string ZK_LINK_LETTER 			= "-";
const std::string ZK_ROOT_PATH        = "/roam";     // zk root path
const std::string SHARE_LOCK          = "/roam/shareLock"; // 分布式锁路径
const int   connectionTimeoutMs = 30000;        // 默认 zk 超时时长
const long  defaultTimeToWait   = 1000L;

const std::string ZK_ISACTIVE          = "isactive";
const std::string ZK_ASSIGN_INPUT_LIST = "assign-input-list";