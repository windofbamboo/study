package com.ai.iot.bill.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Simple to Introduction
 *
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [系统常用函数类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 */
public class SysUtil {
  private final static Logger logger = LoggerFactory.getLogger(SysUtil.class);

  private static InetAddress ia;

  private static String processName = ManagementFactory.getRuntimeMXBean().getName();

  static {
    try {
      ia = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      logger.error("UnknownHostException:{}", e);
      ia = null;
    }
  }

  /**
   * @return 本机主机名
   */
  public static String getHostName() {
    String hostName = "localhost";
    if (ia == null) {
      logger.error("Error getHostName(!)");
    } else {
      hostName = ia.getHostName();
    }
    return hostName;
  }

  /**
   * @return 本机IP
   */
  public static String getHostAddress() {
    String ip = "127.0.0.1";
    if (ia == null) {
      logger.error("Error getHostAddress(!)");

    } else {
      ip = ia.getHostAddress();
    }
    return ip;
  }

  /**
   * 因为主机ip比较多，所以采用主机名的方式 进程实例标识：hostname+'+'+procId
   */
  public static String getProcessInstanceId() {
    return getHostName() + Const.KEY_JOIN + processName.substring(0, processName.indexOf('@'));
  }

  /**
   * 因为主机ip比较多，所以采用主机名的方式 线程实例标识：hostname+'+'+procId+'+'+threadId
   */
  public static String getThreadInstanceId() {
    return getProcessInstanceId() + Const.KEY_JOIN + String.valueOf(Thread.currentThread().getId());
  }

  /**
   * 功能：加密字符串（先简单base64加密，后期修改）。
   *
   * @param str 要加密的字符串
   * @return 加密后的字符串
   * @author 许陆荣
   * @date 2017年06月30日
   */
  public static String encode(String str) {
    return new String(Base64.getEncoder().encode(str.getBytes(Const.UTF8)));
  }

  /**
   * 功能：解密字符串（先简单base64加密，后期修改）。
   *
   * @param str 要解密的字符串
   * @return 解密后的字符串
   * @author 许陆荣
   * @date 2017年06月30日
   */
  public static String decode(String str) {
    return new String(Base64.getDecoder().decode(str));
  }

  /**
   * 功能：将属性中的配置设置到对象字段中。
   *
   * @param obj        要设置的目标对象
   * @param props      来源属性
   * @param decodeKeys 要解密的字段
   * @author 许陆荣
   * @date 2017年06月30日
   */
  public static void setObjProperties(Object obj, Properties props, String... decodeKeys) {
    if (obj != null && props != null && props.size() > 0) {
      // 对加密的属性值解密
      if (decodeKeys != null) {
        for (int i = 0; i < decodeKeys.length; i++) {
          if (props.containsKey(decodeKeys[i])) {
            props.setProperty(decodeKeys[i], SysUtil.decode(props.getProperty(decodeKeys[i])));
          }
        }
      }

      // 设置各项属性值
      for (Entry<Object, Object> prop : props.entrySet()) {
        try {
          Field f = obj.getClass().getDeclaredField(prop.getKey().toString());

          if (f != null) {
            f.setAccessible(true);
            String type = f.getType().toString();
            String val = prop.getValue().toString();
            // System.out.println(f.getName()+" type is "+f.getGenericType());
            if (type.endsWith("String")) {
              f.set(obj, val); // 给属性设值
            } else if (type.endsWith("boolean")) {
              f.set(obj, Boolean.parseBoolean(val));
            } else if (type.endsWith("int") || type.endsWith("Integer")) {
              f.set(obj, Integer.parseInt(val));
            } else if (type.endsWith("long") || type.endsWith("Long")) {
              f.set(obj, Long.parseLong(val));
            }
          }
        } catch (Exception e) {
          // e.printStackTrace();
          logger.debug("Object getDeclaredField NoSuchFieldException:" + prop.getKey());
        }
      }
    }
  }

  public static String getStringValue(Object obj, String defaultValue) {
    if (obj == null) {
      return defaultValue;
    } else {
      return obj.toString();
    }
  }

  public static int getIntValue(String str, int defaultValue) {
    if (StringUtil.isEmpty(str)) {
      return defaultValue;
    } else {
      return Integer.parseInt(str);
    }
  }

  public static Boolean getBoolValue(String str, Boolean defaultValue) {
    if (StringUtil.isEmpty(str)) {
      return defaultValue;
    } else {
      return Boolean.parseBoolean(str);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends IdInterface> Map<Integer, List<T>> getIdMap(List<T> tList) {

    if (tList == null || tList.isEmpty())
      return Collections.EMPTY_MAP;

    Map<Integer, List<T>> connMap = new HashMap<Integer, List<T>>();
    Collections.sort(tList, (o1, o2) -> {
      if (o1.getId() < o2.getId())
        return -1;
      if (o1.getId() > o2.getId())
        return 1;
      return 0;
    });

    List<T> connList = new ArrayList<>();
    T old = null;
    for (T t : tList) {
      if (old == null) {
        connList = new ArrayList<>();
        connList.add(t);
        old = t;
      } else {
        if (old.getId() != t.getId()) {
          connMap.put(old.getId(), connList);
          connList = new ArrayList<>();
          connList.add(t);
          old = t;
        } else {
          connList.add(t);
          old = t;
        }
      }
    }
    if (old != null) {
      connMap.put(old.getId(), connList);
    }
    return connMap;
  }

  // /fileName不带路径,先从$IOT_HOME/conf下面查找文件,再到resource下面查找文件,路径需配置到CLASSPATH环境变量里边
  public static URL getResource(String fileName) {
    URL url;
    // String homeEnv=System.getenv(Const.SYSTEM_HOME);
    // if(CheckNull.isNull(homeEnv)) {
    // url=SysUtil.class.getClassLoader().getResource(fileName);
    // logger.info("url=",url.getRef());
    // return url;
    // }
    // url=SysUtil.class.getClassLoader().getResource(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+fileName);
    // logger.info("url=",url.getRef());
    // return
    // SysUtil.class.getClassLoader().getResource(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+fileName);
    url = SysUtil.class.getClassLoader().getResource(fileName);
    logger.info("url=", url.getRef());
    return url;
  }

  public static InputStream getResourceAsStream(String fileName) {
    URL url;
    // String homeEnv=System.getenv(Const.SYSTEM_HOME);
    // if(CheckNull.isNull(homeEnv)) {
    // url=SysUtil.class.getClassLoader().getResource(fileName);
    // logger.info("url=",url.getRef());
    // return SysUtil.class.getClassLoader().getResourceAsStream(fileName);
    // }
    // url=SysUtil.class.getClassLoader().getResource(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+fileName);
    // logger.info("url=",url.getRef());
    // return
    // SysUtil.class.getClassLoader().getResourceAsStream(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+fileName);
    url = SysUtil.class.getClassLoader().getResource(fileName);
    logger.info("url=", url.getRef());
    return SysUtil.class.getClassLoader().getResourceAsStream(fileName);
  }

  /**
   * 根据起始资源或队列编号和应用并发总数,再去除已经分配使用的资源或队列列表,产生当前应用需要读写的资源或队列编号
   *
   * @param resBegin    资源起始位置,注意是闭区间
   * @param resEnd      资源结束位置,注意是闭区间
   * @param appNum      应用总并发个数
   * @param usedNumList 已经分配使用的资源列表
   */
  public static List<Integer> getBalanceNumList(List<Integer> resNumList, int appNum, Set<Integer> usedNumList) {
    List<Integer> resultNumList = new ArrayList<Integer>();
    int avg = (resNumList.size() + appNum) / appNum;// 为保证均衡,直接进位
    if (avg == 0)
      avg = 1;
    for (Integer i : resNumList) {
      if (usedNumList.contains(i)) {
        continue;
      }
      resultNumList.add(i);
      if (resultNumList.size() == avg) {// 达到均值了
        return resultNumList;
      }
    }
    return resultNumList;
  }

  public static List<Integer> getBalanceNumList(int resBegin, int resEnd, int appNum, Set<Integer> usedNumList) {
    List<Integer> numList = new ArrayList<Integer>();
    for (int i = 0; i <= resEnd; i++) {
      numList.add(i);
    }
    return getBalanceNumList(numList, appNum, usedNumList);
  }

  public static List<String> getBalanceStringList(List<String> resStringList, int appNum, Set<String> usedStringList) {
    List<String> resultStringList = new ArrayList<String>();
    int avg = (resStringList.size() + appNum) / appNum;// 为保证均衡,直接进位
    if (avg == 0)
      avg = 1;
    for (String res : resStringList) {
      if (usedStringList.contains(res)) {
        continue;
      }
      resultStringList.add(res);
      if (resultStringList.size() == avg) {// 达到均值了
        return resultStringList;
      }
    }
    return resultStringList;
  }
}
