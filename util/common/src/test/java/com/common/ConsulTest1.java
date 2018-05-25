package com.common;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.agent.model.Service;
import org.junit.Test;

import java.util.Map;

public class ConsulTest1 {

    //consul-api
    @Test
    public void serviceTest1() throws Exception {
        ConsulRawClient client = new ConsulRawClient("192.168.0.126", 8500);
        ConsulClient consul = new ConsulClient(client);

        //获取所有服务
        Map<String, Service> map = consul.getAgentServices().getValue();
    }




}
