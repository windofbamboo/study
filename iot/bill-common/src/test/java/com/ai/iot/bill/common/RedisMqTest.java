package com.ai.iot.bill.common;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import com.ai.iot.bill.common.mq.RedisMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;

class MqTest implements Serializable {
    private String name = null;

    private int value = 0;

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}

public class RedisMqTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testRedisMqStr() {
        int routeID = BaseDefine.CONNTYPE_REDIS_DEVINFO;
        RedisMq sender = new RedisMq(routeID);
        RedisMq recver = new RedisMq(routeID);

        // 字符串测试
        sender.sendMessage("strTest", "hello world 1");
        sender.sendMessage("strTest", "hello world 2");
        String resStr1 = recver.recvMsg("strTest", 10);
        String resStr2 = recver.recvMsg("strTest", 10);
        System.out.println(resStr1);
        System.out.println(resStr2);
    }

    @Test
    public void testRedisMqObject() {
        int routeID = BaseDefine.CONNTYPE_REDIS_DEVINFO;
        RedisMq sender = new RedisMq(routeID);
        RedisMq recver = new RedisMq(routeID);

        // 对象测试
        MqTest test1 = new MqTest();
        test1.setName("zf");
        test1.setValue(1);
        MqTest test2 = new MqTest();
        test2.setName("ai");
        test2.setValue(2);

        sender.sendMessageObject("strTest", test1);
        sender.sendMessageObject("strTest", test2);
        MqTest resObj1 = (MqTest) recver.recvMsgObject("strTest", 10);
        MqTest resObj2 = (MqTest) recver.recvMsgObject("strTest", 10);
        System.out.println(resObj1.getName() + " " + resObj1.getValue());
        System.out.println(resObj2.getName() + " " + resObj2.getValue());
    }

    @Test
    public void testRedisMqArray() {
        int routeID = BaseDefine.CONNTYPE_REDIS_DEVINFO;
        RedisMq sender = new RedisMq(routeID);
        RedisMq recver = new RedisMq(routeID);

        // 数组测试
        byte[] testBy1 = "wo ai tian an men".getBytes(Const.UTF8);
        byte[] testBy2 = "tian an men shang tai yang sheng".getBytes(Const.UTF8);
        sender.sendMessageBytes("strTest", testBy1);
        sender.sendMessageBytes("strTest", testBy2);
        byte[] resBy1 = recver.recvMsgByte("strTest", 10);
        byte[] resBy2 = recver.recvMsgByte("strTest", 10);
        System.out.println(new String(resBy1, Const.UTF8));
        System.out.println(new String(resBy2, Const.UTF8));
    }

}
