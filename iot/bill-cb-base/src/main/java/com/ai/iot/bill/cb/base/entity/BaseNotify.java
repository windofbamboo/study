package com.ai.iot.bill.cb.base.entity;

import com.ai.iot.bill.common.util.JSONUtil;
import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 对外接口消息发送基类
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/12 09:36]
 * @Version:      [v1.0]
 */
public class BaseNotify implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(BaseNotify.class);
    private static final long serialVersionUID = 2017090514190957001L;

    //消息来源类型：WEB API 其他
    public static enum OriginType {
        web,
        API,
        other
    }

    private static Map<String, Class> notifyMap = new HashMap<>();

    //接口名称
    @JSONField(name = "INTERFACE_NAME")
    private String interfaceName;

    //通知的来源类型
    @JSONField(name = "ORIGIN_TYPE")
    private OriginType originType = OriginType.other;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public OriginType getOriginType() {
        return originType;
    }

    public void setOriginType(OriginType originType) {
        this.originType = originType;
    }

    public String toString() {
        return JSONUtil.toJson(this);
    }

    public static void addNotifyClass(String notifyName, Class clazz) {
        notifyMap.put(notifyName, clazz);
    }

    public static Class getNotifyClass(String notifyName) {
        return notifyMap.get(notifyName);
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
}
