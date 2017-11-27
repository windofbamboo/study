package com.ai.iot.bill.common.mq;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.util.ByteUtil;
import com.ai.iot.bill.common.util.Const;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;


public class RedisMq {

  private static final Logger logger = LoggerFactory.getLogger(RedisMq.class);
  private CustJedisCluster jc = null;

  /**
   * @Author zhangfeng
   * @Desciption 初始化
   */
  public RedisMq(CustJedisCluster jc) {
    this.jc = jc;
  }


  /**
   * Creates a new instance of RedisMq.
   * Description
   *
   * @param connType BaseDefine里面定义的ConnectType
   */
  public RedisMq(int connType) {
    this.jc = RedisMgr.getJedisCluster(connType);
  }

  /**
   * @Author zhangfeng
   * @Desciption 断开连接
   */
  public void disconnect() {

    try {
      if (jc != null) {
        jc.close();
        jc = null;
      }
    } catch (Exception e) {
      logger.error("Faild to disconnect.", e);
    }
  }

  /**
   * @Author zhangfeng
   * @Desciption 发送消息：字符串
   */
  public static long sendMessage(CustJedisCluster jc, String topic, String msg) {
    long ret = -1;
    if (jc != null) {
      ret = jc.lpush(topic.getBytes(Const.UTF8), msg.getBytes(Const.UTF8));
    }
    return ret;
  }

  /**
   * @Author: zhangfeng
   * @Desciption:
   * @Date: 16:58 2017/7/10
   */
  public long sendMessage(String topic, String msg) {
    return sendMessage(jc, topic, msg);
  }

  /**
   * @Author zhangfeng
   * @Desciption 发送消息：对象
   */
  public static long sendMessageObject(CustJedisCluster jc, String topic, Serializable msg) {
    long ret = -1;
    if (jc != null) {
      ret = jc.lpush(topic.getBytes(Const.UTF8), ByteUtil.getBytes(msg));
    }
    return ret;
  }

  /**
   * @Author: zhangfeng
   * @Desciption:
   * @Date: 16:58 2017/7/10
   */
  public long sendMessageObject(String topic, Serializable msg) {
    return sendMessageObject(jc, topic, msg);
  }

  /**
   * @Author zhangfeng
   * @Desciption 发送消息：数组
   */
  public static long sendMessageBytes(CustJedisCluster jc, String topic, byte[] msg) {
    long ret = -1;
    if (jc != null) {
      ret = jc.lpush(topic.getBytes(Const.UTF8), msg);
    }
    return ret;
  }

  /**
   * @Author: zhangfeng
   * @Desciption:
   * @Date: 17:00 2017/7/10
   */
  public long sendMessageBytes(String topic, byte[] msg) {
    return sendMessageBytes(jc, topic, msg);
  }

  /**
   * @Author zhangfeng
   * @Desciption 返回消息：字符串
   */
  public static String recvMsg(CustJedisCluster jc, String topic, int timeout) {
    String ret = null;
    if (jc != null) {
      List<byte[]> result = jc.brpop(timeout, topic.getBytes(Const.UTF8));
      if (result != null && result.size() == 2) {
        ret = new String(result.get(1), Const.UTF8);
      }
    }

    return ret;
  }

  /**
   * @Author: zhangfeng
   * @Desciption:
   * @Date: 17:02 2017/7/10
   */
  public String recvMsg(String topic, int timeout) {
    return recvMsg(jc, topic, timeout);
  }

  /**
   * @Author zhangfeng
   * @Desciption 获取消息：对象
   */
  public static Object recvMsgObject(CustJedisCluster jc, String topic, int timeout) {
    Object ret = null;
    if (jc != null) {
      List<byte[]> result = jc.brpop(timeout, topic.getBytes(Const.UTF8));
      if (result != null && result.size() == 2) {
        ret = ByteUtil.getObject(result.get(1));
      }
    }

    return ret;
  }

  /**
   * @Author: zhangfeng
   * @Desciption:
   * @Date: 17:03 2017/7/10
   */
  public Object recvMsgObject(String topic, int timeout) {
    return recvMsgObject(jc, topic, timeout);
  }


  /**
   * @Author zhangfeng
   * @Desciption 获取消息：数组
   */
  public static byte[] recvMsgByte(CustJedisCluster jc, String topic, int timeout) {
    byte[] ret = null;
    if (jc != null) {
      List<byte[]> result = jc.brpop(timeout, topic.getBytes(Const.UTF8));
      if (result != null && result.size() == 2) {
        ret = result.get(1);
      }
    }

    return ret;
  }

  /**
   * @Author: zhangfeng
   * @Desciption:
   * @Date: 17:04 2017/7/10
   */
  public byte[] recvMsgByte(String topic, int timeout) {
    return recvMsgByte(jc, topic, timeout);
  }


  /**
   * @Author zhangfeng
   * @Desciption 判断是否连接
   */
  public boolean isConnected() {
    if (jc != null) {
      String ok = new String(jc.echo("11".getBytes(Const.UTF8)), Const.UTF8);
      if (ok.equals("11")) {
        return true;
      }
    }

    return false;
  }

}