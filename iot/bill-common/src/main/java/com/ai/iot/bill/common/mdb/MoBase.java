package com.ai.iot.bill.common.mdb;

import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public abstract class MoBase {
  private static final Logger logger = LoggerFactory.getLogger(MoBase.class);
  public final static String separator = Const.KEY_JOIN;
  protected List<MoContainer> relatedMoContainers;

  public List<MoContainer> getRelatedMoContainers() {
    return relatedMoContainers;
  }

  public void setRelatedMoContainers(List<MoContainer> relatedMoContainers) {
    this.relatedMoContainers = relatedMoContainers;
  }

  public MoBase() {
  }

  //MDB读相关
  public abstract String getField();

  public abstract int getFieldType();

  /**
   * 数据打印的格式，逗号分割，取自类的json格式，继承的子类不需要覆盖
   */
  public String toString() {
    return JSONUtil.toJson(this);
  }

  public boolean isExpire() {
    return false;
  }

  public String getName() {
    return this.getClass().getSimpleName();
  }

  public byte[] generateKey(Object catalog, Object key) {
    StringBuffer sb = new StringBuffer();
    sb.append(catalog).append(separator).append(key);
    return sb.toString().getBytes(Const.UTF8);
  }

  public static String toString(Object obj, Class<?> clazz) {
    if(obj == null){
      return "";
    }

    Field[] fields = clazz.getDeclaredFields();// 根据Class对象获得属性 私有的也可以获得
    String s = "";
    try {
      for (Field f : fields) {
        f.setAccessible(true); // 设置些属性是可以访问的
        Object val = f.get(obj); // 得到此属性的值
        String name = f.getName(); // 得到此属性的名称
        s += name + ":" + val + ",";
      }
    } catch (IllegalAccessException e) {
      logger.error("########MoBase::toString() Exception.##########");
    }
    return s;
  }

  //持否是抽象出来的包装类，例如MoAutoRuleWrapper、MoAutoruleOperContWrapper
  //什么样的是包装类：一个MO在资料加载或者同步时，分发的ID与该类的标识ID（主键ID）不是同一个，
  //例如MoAutoRule，其在加载或者同步时，分发的ID，是ACCT_ID，而其本身主键ID是RULE_ID。此时就需要抽象出一个包装类MoAutoRuleWrapper
  //为的是在分发ID时，MoAutoRuleWrapper有且只有一个，并且根据MoAutoRuleWrapper的主键ID，再去加载多个相关MO。
  public boolean isWrapper() {
    return false;
  }

  public int getSelfId() {
    return -1;
  }
}
