package com.common;

import com.common.util.ZkWrapper;
import org.junit.Test;

/**
 * Zookeeper监听使用样例
 */
public class ZKwatcherTest {
	private static final String ZK_CONNECT_LIST="192.168.0.126:2181,127.0.0.1:2181";
	private static final String ZK_PATH="ZKwatcherTest";
    //1.path Cache
    // 监听子节点   --节点创建、删除、数据修改
    @Test
    public void setListenterChildrenNode() throws Exception{

    	ProcessTypeCallBack processCallBack = new ProcessTypeCallBack();
        ZkWrapper zkWrapper = new ZkWrapper(ZK_CONNECT_LIST,ZkWrapper.ROOT_DEFAULT_NAME);
        zkWrapper.setListenterChildrenNode(processCallBack,ZK_PATH+ZkWrapper.ZK_PATH_SEP+"child");
        zkWrapper.deleteNode(ZK_PATH+ZkWrapper.ZK_PATH_SEP+"child");
        zkWrapper.insertNode(ZK_PATH+ZkWrapper.ZK_PATH_SEP+"child");
//        for(;;){
//            Thread.sleep(15000);
//        }
    }

    // 监听指定节点 --节点创建、删除、数据修改
    @Test
    public void setListenterNode() throws Exception{
        ProcessCallBack processCallBack = new ProcessCallBack();
        ZkWrapper zkWrapper = new ZkWrapper(ZK_CONNECT_LIST,ZkWrapper.ROOT_DEFAULT_NAME);
        zkWrapper.setListenterNode(processCallBack,ZK_PATH);
        zkWrapper.deleteNode(ZK_PATH);
        zkWrapper.insertNode(ZK_PATH);
//        for(;;){
//            Thread.sleep(15000);
//        }
    }

    // 监听节点下所有
    // 指定节点 --节点创建、删除、数据修改
    // 子节点   --节点创建、删除、数据修改
    // 多级子节点 --节点创建、删除、数据修改
    @Test
    public void setListenterTree() throws Exception{
    	ProcessTypeCallBack processCallBack = new ProcessTypeCallBack();
        ZkWrapper zkWrapper = new ZkWrapper(ZK_CONNECT_LIST,ZkWrapper.ROOT_DEFAULT_NAME);
        zkWrapper.setListenterTree(processCallBack,ZK_PATH+ZkWrapper.ZK_PATH_SEP+"child");
        zkWrapper.deleteNode(ZK_PATH+ZkWrapper.ZK_PATH_SEP+"child");
        zkWrapper.insertNode(ZK_PATH+ZkWrapper.ZK_PATH_SEP+"child");
//        for(;;){
//            Thread.sleep(15000);
//        }
    }


    public class ProcessCallBack implements ZkWrapper.ZkWatcherCallBack{
        public void updateZkInfo(String node, String data){
            System.out.println("执行回调函数1");
            System.out.println("NODE : " + node + "  数据:" + data);
        }
    }
    public class ProcessTypeCallBack implements ZkWrapper.ZkWatcherTypeCallBack{
        public void updateZkInfo(ZkWrapper.Type type, String node, String data){
            System.out.println("执行回调函数2");
            switch (type) {
                case ADDED:
                    System.out.println("NODE_ADDED : " + node + "  数据:" + data);
                    break;
                case UPDATED:
                    System.out.println("NODE_REMOVED : " + node + "  数据:" + data);
                    break;
                case REMOVED:
                    System.out.println("NODE_UPDATED : " + node + "  数据:" + data);
                    break;
                default:
                    break;
            }
        }
    }



}


