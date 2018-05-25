package com.common;

import com.daedafusion.jetcd.EtcdResult;
import com.daedafusion.jetcd.impl.EtcdClientImpl;
import org.junit.Test;

public class JetcdTest {

    @Test
    public void NodeTest() throws Exception {

        EtcdClientImpl etcdClient = new EtcdClientImpl("http://192.168.0.126:2379");
        //节点
        EtcdResult etcdResult = etcdClient.get("foo");

        etcdClient.delete("/test/name");

        etcdClient.set("/test/name","123");

        etcdClient.set("test/name","fd",new Integer(5));

        //目录
        etcdClient.refreshDirectory("test/name",new Integer(5));

        etcdClient.createDirectory("test");

        etcdClient.createDirectory("test",new Integer(5));

        etcdClient.listDirectory("test");

        etcdClient.deleteDirectory("test");

        etcdClient.deleteDirectoryRecursive("test");
        //原子操作
        etcdClient.compareAndSet("/test/name","123","abc");

        etcdClient.compareAndSet("/test/name",new Integer(2),"abc");

        etcdClient.compareAndDelete("/test/name","123");

        etcdClient.compareAndDelete("/test/name",new Integer(2));
        //watch
        etcdClient.watch("/test/name");

        etcdClient.watch("/test/name",true);

        etcdClient.watch("/test/name",new Long(1),true);
        //queue
        etcdClient.queue("/test/name","234");
        etcdClient.getQueue("/test/name");

        etcdClient.close();
    }
}
