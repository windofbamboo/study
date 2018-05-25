package com.common;

import mousio.client.retry.RetryOnce;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdVersionResponse;
import org.junit.Test;

import java.net.URI;

public class Etcd4jTest {

    @Test
    public void NodeTest() throws Exception {

        URI[] uris = new URI[]{new URI("http://192.168.0.126:2379")};
//        uris[0] = URI.create("192.168.0.126:2379");
        EtcdClient client = new EtcdClient (uris);
        client.setRetryHandler(new RetryOnce(600)); //retry策略

        EtcdVersionResponse response1 = client.version();


        EtcdKeysResponse response = client.get("/test/name").send().get();

        System.out.println(response.node.value);
    }



}
