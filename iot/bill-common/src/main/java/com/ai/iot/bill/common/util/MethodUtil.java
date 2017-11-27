package com.ai.iot.bill.common.util;

import shade.storm.com.google.common.collect.Lists;
import shade.storm.com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qianwx
 */
public class MethodUtil {
  private static final ThreadLocal<MethodLog> threadLocal = new ThreadLocal<>();

  private static MethodLog getMethodLog() {
    MethodLog log = threadLocal.get();
    if (log == null) {
      log = new MethodLog();
      threadLocal.set(log);
    }
    return log;
  }

  public static void start(String methodName) {
    getMethodLog().setStartTime(methodName);
  }

  public static void end(String methodName) {
    getMethodLog().setEndTime(methodName);
  }

  public static void print() {
    getMethodLog().printTime();
  }
}

class MethodLog {
  private Map<String, List<Integer>> methodInvokeTime = Maps.newHashMap();

  private Map<String, Long> methodStartTime = Maps.newHashMap();

  public void printTime() {
    if (methodInvokeTime.isEmpty()) {
      System.out.println("error");
    }
    methodInvokeTime.forEach((k, v) -> {
      v = v.stream().sorted(Comparator.comparingInt(Integer::intValue)).collect(Collectors.toList());
      int size = v.size();
      Integer total = v.stream().mapToInt(Integer::intValue).sum();
      System.out.println("method:" + k + ",maxTime:" + v.get(0) + ",minTime:" + v.get(size - 1) + ",execTimes:" + size + ",avgTime:" + (total / size));
    });
  }

  public void setStartTime(String methodName) {
    methodStartTime.put(methodName, System.currentTimeMillis());
  }

  public void setEndTime(String methodName) {
    List<Integer> durations = methodInvokeTime.get(methodName);
    long now = System.currentTimeMillis();
    long start = methodStartTime.get(methodName);
    if (durations == null) {
      durations = Lists.newArrayList();
    }
    durations.add(Integer.valueOf((now - start) + ""));
    methodInvokeTime.put(methodName, durations);
  }
}
