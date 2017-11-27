package com.ai.iot.bill.common.paramcachemanager.pog.sys.pos;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoStatus extends PoBase implements Serializable {

    private static final long serialVersionUID = 0l;

    @JSONField(name = "stateCode")
    private int stateCode;
    @JSONField(name = "stateName")
    private String stateName;
    @JSONField(name = "startDate")
    private Long startDate;
    @JSONField(name = "endDate")
    private Long endDate;

    @Override
    public boolean fillData(Object obj) {
        @SuppressWarnings("unchecked")
        List<String> fields = (List<String>) obj;
        stateCode = Integer.valueOf(fields.get(0));
        stateName = String.valueOf(fields.get(1));
        Long _startDate = Long.valueOf(fields.get(2));
        if (null != _startDate && !"".equals(_startDate)) {
            startDate = Long.valueOf(_startDate);
        } else {
            startDate = 0l;
        }
        Long _endDate = Long.valueOf(fields.get(3));
        if (null != _endDate && !"".equals(_endDate)) {
            endDate = Long.valueOf(_endDate);
        } else {
            endDate = 0l;
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
        return String.valueOf(this.stateCode);
    }

    public static String getIndex1Name() {
        return "getIndex1Key";
    }

    public int getStateCode() {
        return stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public Long getStartDate() {
        return startDate;
    }

    public Long getEndDate() {
        return endDate;
    }
}
