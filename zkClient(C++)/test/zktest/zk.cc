#include <stdbool.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <zookeeper.h>
#include <zookeeper_log.h>
#include <iostream>

using namespace std;

struct ZKWatchContext {
	ZKWatchContext(int* count, void* context){
		this->context = context;
		this->count = count;
	};

	int*  count;
	void* context;
};


void QueryServer_watcher_g(zhandle_t* zh, int type, int state,const char* path, void* watcherCtx)
{
	ZKWatchContext* data = (ZKWatchContext*)watcherCtx;
	int* count = data->count;
	
	cout<<"count = "<<*count<<endl;
	(*count)++;
	
	if (type == ZOO_SESSION_EVENT) {
		if (state == ZOO_CONNECTED_STATE) {
			cout<<"[[[QueryServer]]] Connected to zookeeper service successfully!"<<endl;
		} else if (state == ZOO_EXPIRED_SESSION_STATE) {
			cout<<"Zookeeper session expired!"<<endl;
		} else{
			cout<<"Connected to zookeeper fail!"<<endl;
		}
	}
}

void QueryServer_string_completion(int rc, const char *name, const void *watcherCtx)
{
	ZKWatchContext* data = (ZKWatchContext*)watcherCtx;
	int* count = data->count;
	(*count)++;
	
	if (!rc) {
		cout<<"rc = "<<rc<<"name = "<<name<<endl;
	}
}

void QueryServer_accept_query()
{
	cout<<"QueryServer is running..."<<endl;
}

int main(int argc, const char *argv[])
{
	const char* host = "10.21.20.38:32000";
	int timeout = 30000;
	zoo_set_debug_level(ZOO_LOG_LEVEL_WARN);
	
	int count =0;
	ZKWatchContext* watch_ctx = new ZKWatchContext(&count, NULL);
	
	zhandle_t* zkhandle = zookeeper_init(host,QueryServer_watcher_g, timeout, NULL, watch_ctx, 0);
	if (zkhandle == NULL) {
		cout<<"Error when connecting to zookeeper servers..."<<endl;
		exit(EXIT_FAILURE);
	}
	sleep(1);
	cout<<"count:"<<count<<endl;
	
	// struct ACL ALL_ACL[] = {{ZOO_PERM_ALL, ZOO_ANYONE_ID_UNSAFE}};
	// struct ACL_vector ALL_PERMS = {1, ALL_ACL};
	int ret = zoo_acreate(zkhandle, "/QueryServer", "alive", 5,
												&ZOO_OPEN_ACL_UNSAFE, ZOO_EPHEMERAL,
												QueryServer_string_completion, watch_ctx);
	if (ret) {
		//fprintf(stderr, "Error %d for %s\n", ret, "acreate");
		cout<<"Error "<<ret<<" for acreate"<<endl;
		exit(EXIT_FAILURE);
	}
	sleep(1);
	cout<<"count:"<<count<<endl;
	
	int session_timeout_ = zoo_recv_timeout(zkhandle);
	cout<<"timeout:"<<session_timeout_<<endl;
	
	do {
		// 模拟 QueryServer 对外提供服务.
		// 为了简单起见, 我们在此调用一个简单的函数来模拟 QueryServer.
		// 然后休眠 5 秒，程序主动退出(即假设此时已经崩溃).
		QueryServer_accept_query();
		sleep(60);
	} while(false);
	
	zookeeper_close(zkhandle);
	
	cout<<"count:"<<count<<endl;
}