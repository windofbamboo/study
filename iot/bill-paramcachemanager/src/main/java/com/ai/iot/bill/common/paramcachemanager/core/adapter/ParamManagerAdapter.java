package com.ai.iot.bill.common.paramcachemanager.core.adapter;

import com.ai.iot.bill.common.config.ParamManagerAdapterInterface;

public class ParamManagerAdapter implements ParamManagerAdapterInterface {
    private Object paramManager;

    @Override
    public Object getParam(String paramName) {
        return null;
    }

    public Object getParamManager() {
        return paramManager;
    }

    public void setParamManager(Object paramManager) {
        this.paramManager = paramManager;
    }
}
