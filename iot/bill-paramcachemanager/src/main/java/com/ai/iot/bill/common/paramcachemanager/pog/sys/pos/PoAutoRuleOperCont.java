package com.ai.iot.bill.common.paramcachemanager.pog.sys.pos;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.alibaba.fastjson.annotation.JSONField;
import shade.storm.org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qianwx
 */
public class PoAutoRuleOperCont extends PoBase implements Serializable {

  private static final long serialVersionUID = 2384383354753254883L;
  @JSONField(name = "ruleId")
  private long ruleId;// key
  @JSONField(name = "ruleVerId")
  private int ruleVerId;
  @JSONField(name = "operContId")
  private long operContId;
  @JSONField(name = "operId")
  private int operId;
  @JSONField(name = "operType")
  private int operType;
  @JSONField(name = "operCont1")
  private String operCont1;
  @JSONField(name = "operCont2")
  private String operCont2;
  @JSONField(name = "operCont3")
  private String operCont3;
  @JSONField(name = "followOperMode")
  private int followOperMode;
  @JSONField(name = "followFixtimeType")
  private int followFixtimeType;
  @JSONField(name = "followFixtimeArg")
  private String followFixtimeArg;
  @JSONField(name = "updateTime")
  private long updateTime;

  @Override
  public boolean fillData(Object obj) {
    @SuppressWarnings("unchecked")
    List<String> fields = (List<String>) obj;
    String ruleIdStr = fields.get(0);
    if (StringUtils.isNotBlank(ruleIdStr)) {
      ruleId = Long.valueOf(ruleIdStr);
    } else {
      ruleId = -1;
    }
    String verIdStr = fields.get(1);
    if (StringUtils.isNotBlank(verIdStr)) {
      ruleVerId = Integer.valueOf(verIdStr);
    } else {
      ruleVerId = 0;
    }
    String operContIdStr = fields.get(2);
    if (StringUtils.isNotBlank(operContIdStr)) {
      operContId = Long.valueOf(operContIdStr);
    } else {
      operContId = 0;
    }
    String operIdStr = fields.get(3);
    if (StringUtils.isNotBlank(operIdStr)) {
      operId = Integer.valueOf(operIdStr);
    } else {
      operId = 0;
    }
    String operTypeStr = fields.get(4);
    if (StringUtils.isNotBlank(operTypeStr)) {
      operType = Integer.valueOf(operTypeStr);
    } else {
      operType = 0;
    }
    operCont1 = fields.get(5);
    operCont2 = fields.get(6);
    operCont3 = fields.get(7);
    String followOperModeStr = fields.get(0);
    if (StringUtils.isNotBlank(followOperModeStr)) {
      followOperMode = Integer.valueOf(followOperModeStr);
    } else {
      followOperMode = 0;
    }
    String followFixtimeTypeStr = fields.get(9);
    if (StringUtils.isNotBlank(followFixtimeTypeStr)) {
      followFixtimeType = Integer.valueOf(followFixtimeTypeStr);
    } else {
      followFixtimeType = 0;
    }
    followFixtimeArg = fields.get(10);
    String updateTimeStr = fields.get(11);
    if (StringUtils.isNotBlank(updateTimeStr)) {
      updateTime = Long.valueOf(updateTimeStr);
    } else {
      updateTime = 0;
    }
    return true;
  }

  @Override
  public String getPoGroupName() {
    return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.toString();
  }

  @Override
  public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
    Map<String, Method> indexMethods = new HashMap<String, Method>();
    indexMethods.put(getIndex1Name(), getClass().getMethod(getIndex1Name()));
    return indexMethods;
  }

  public String getIndex1Key() {
    return String.valueOf(this.ruleId);
  }

  public static String getIndex1Name() {
    return "getIndex1Key";
  }

  public long getRuleId() {
    return ruleId;
  }

  public int getRuleVerId() {
    return ruleVerId;
  }

  public long getOperContId() {
    return operContId;
  }

  public int getOperId() {
    return operId;
  }

  public int getOperType() {
    return operType;
  }

  public String getOperCont1() {
    return operCont1;
  }

  public String getOperCont2() {
    return operCont2;
  }

  public String getOperCont3() {
    return operCont3;
  }

  public int getFollowFixtimeType() {
    return followFixtimeType;
  }

  public String getFollowFixtimeArg() {
    return followFixtimeArg;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public int getFollowOperMode() {
    return followOperMode;
  }
}
