package com.common;

import com.common.util.ZkWrapper;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;

/**
 * Zookeeper使用样例
 * 包括：节点创建，数据写入，数据修改，节点删除
 */
public class ZookeeperTest {

    /**
     * 永久节点创建，数据写入，数据修改，节点删除
     */
    @Test
    public void NodeTest() throws Exception {

        ZkWrapper zkWrapper = new ZkWrapper("192.168.0.126:2181",ZkWrapper.ROOT_DEFAULT_NAME);

        //创建节点
        if(!zkWrapper.exists("register")){
            zkWrapper.insertNode("register");
        }
        if(!zkWrapper.exists("register/sender")){
            zkWrapper.insertNode("register/sender");
        }
        if(!zkWrapper.exists("register/sender/sender1")){
            zkWrapper.insertNode("register/sender/sender1");
        }

        //设置节点数据
        zkWrapper.updateNodeValue("register/sender/sender1","12321321");
        //获取数据
        String data = zkWrapper.getNodeValue("register/sender/sender1");
        System.out.println(data);

        zkWrapper.deleteNode("register");
        //关闭链接
        zkWrapper.closeConn();
    }

    /**
     * 临时节点创建，数据写入，数据修改，临时节点删除
     */
    @Test
    public void TempNodeTest() throws Exception {

        ZkWrapper zkWrapper = new ZkWrapper("192.168.0.126:2181",ZkWrapper.ROOT_DEFAULT_NAME);

        if(!zkWrapper.isTempNodeExist("test")){
            zkWrapper.insertTempNode("test", "343243");
            String data = zkWrapper.getNodeValue("test");
            System.out.println(data);
            zkWrapper.updateNodeValue("test","a");
            data = zkWrapper.getNodeValue("test");
            System.out.println(data);


            zkWrapper.deleteTempNode("test");
        }
        zkWrapper.closeConn();
    }

    /**
     * 子节点获取
     */
    @Test
    public void getChildNodeList() throws Exception {
        ZkWrapper zkWrapper = new ZkWrapper("192.168.0.126:2181",ZkWrapper.ROOT_DEFAULT_NAME);

        //创建节点
        if(!zkWrapper.exists("register")){
            zkWrapper.insertNode("register");
        }
        if(!zkWrapper.exists("register/sender")){
            zkWrapper.insertNode("register/sender");
        }
        if(!zkWrapper.exists("register/sender/sender1")){
            zkWrapper.insertNode("register/sender/sender1");
            zkWrapper.insertNode("register/sender/sender2");
            zkWrapper.insertNode("register/sender/sender3");
            zkWrapper.insertNode("register/sender/sender4");
            zkWrapper.insertNode("register/sender/sender5");
            zkWrapper.insertNode("register/sender/sender6");
        }

        List<String> tList = zkWrapper.getChildNodeList("register/sender");
        zkWrapper.closeConn();
        System.out.println(tList);
    }

    /**
     * 测试lock方法
     */
    @Test
    public void lockTest() throws Exception {

        ZkWrapper zkWrapper = new ZkWrapper("192.168.0.126:2181",ZkWrapper.ROOT_DEFAULT_NAME);
        zkWrapper.lock("res_balance_manager_state/isactive");

        zkWrapper.unlock();
    }


    /**
     * 测试多线程下lock表现
     */
    @Test
    public void mutiLockTest() throws Exception {

        ExecutorService tPool = Executors.newFixedThreadPool(2);
        CompletionService<String> completionService = new ExecutorCompletionService<>(tPool);

        int i=0;
        completionService.submit(new LockBean("a1","res_balance_manager_state/isactive"));i++;
        completionService.submit(new LockBean("a2","res_balance_manager_state/isactive"));i++;
        for(;i>0;i-- ){
            try {
                String lockResult = completionService.take().get();
                System.out.println(lockResult);

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class LockBean implements Callable<String> {

        private String instanceId;
        @SuppressWarnings("unused")
		private String lockPath;

        public LockBean(String instanceId, String lockPath) {
            this.instanceId = instanceId;
            this.lockPath = lockPath;
        }

        @Override
        public String call() {

            ZkWrapper zkWrapper = new ZkWrapper("192.168.0.126:2181",ZkWrapper.ROOT_DEFAULT_NAME);
            InterProcessMutex interProcessMutex = null;
            try {
                interProcessMutex = zkWrapper.getDistributedLock("res_balance_manager_state/isactive");
            } catch (Exception e) {
                return instanceId+" get interProcessMutex fail";
            }
            boolean result;
            try {
                //lock
                result = interProcessMutex.acquire(3,TimeUnit.SECONDS);
            } catch (Exception e) {
                return instanceId+ " interProcessMutex acquire fail";
            }

            boolean lock = interProcessMutex.isAcquiredInThisProcess();
            boolean own = interProcessMutex.isOwnedByCurrentThread();

            //unlock
            zkWrapper.closeConn();

            return instanceId+" result is "+ result +", lock is "+ lock +" ,own is "+ own;
        }
    }


}
