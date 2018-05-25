package com.common.util;

import mousio.client.retry.RetryOnce;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class EtcdUtil {

    private EtcdClient client;
    private Properties property;
    /**
     * serverName
     * roomServerList
     * zeonId
     * etcdGroupNodeCount
     * etcdHost
     * serverIp
     * serverPort
    **/
    private String etcdKey ; // 这里就是发布的节点
    private String node ;

    public EtcdUtil(Properties property) {
        this.property = property;
        this.etcdKey = "/roomServerList"+"/"+property.getProperty("zeonId") +"/"+ property.getProperty("serverName");
        this.node  = etcdKey + "_" + property.getProperty("serverIp") + "_" + property.getProperty("serverPort");

        int nodeCount = Integer.parseInt(property.getProperty("etcdGroupNodeCount"));

        URI[] uris = new URI[nodeCount]; // 对于集群，把所有集群节点地址加进来，etcd的代码里会轮询这些地址来发布节点，直到成功
        for (int iter = 0; iter < nodeCount; iter++) {
            String urlString = property.getProperty("etcdHost" + new Integer(iter).toString());
            System.out.println(urlString);
            uris[iter] = URI.create(urlString);
        }
        client = new EtcdClient(uris);
        client.setRetryHandler(new RetryOnce(20)); //retry策略
    }

    public void regist() { // 注册节点，放在程序启动的入口
        try { // 用put方法发布一个节点
            EtcdResponsePromise<EtcdKeysResponse> p = client.putDir(this.node).ttl(60).send();
            p.get(); // 加上这个get()用来保证设置完成，走下一步，get会阻塞，由上面client的retry策略决定阻塞的方式

            new Thread(new GuardEtcd()).start(); // 启动一个守护线程来定时刷新节点
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void destory() {
        try {
            EtcdResponsePromise<EtcdKeysResponse> p = client.deleteDir(this.node).recursive().send();
            p.get();
            client.close();
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class GuardEtcd implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(40*1000l);
                    client.refresh(node,60).send();
                } catch (IOException | InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }



}
