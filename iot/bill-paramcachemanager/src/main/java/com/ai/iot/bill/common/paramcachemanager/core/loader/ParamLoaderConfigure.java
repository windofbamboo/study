package com.ai.iot.bill.common.paramcachemanager.core.loader;

import com.ai.iot.bill.common.config.UniversalConstant;

import static com.ai.iot.bill.common.config.UniversalConstant.ParamLoadConst.PARAM_LOAD_JC_ROUTE_ID;

/**
 * 参数加载器的配置信息，包括扫描间隔时间 加载时REDIS的路由信息等
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/10 09:36]
 * @Version:      [v1.0]
 */
public class ParamLoaderConfigure {
    private int scanSleepSeconds = UniversalConstant.ParamLoadConst.SCAN_SLEEP_SECONDS;
    private String pdbLoginUrl = "";
    private String mdbConnecCodeName = "";
    private int paramLoadMdbRouteType = PARAM_LOAD_JC_ROUTE_ID;
    public ParamLoaderConfigure() {

    }

    public int getScanSleepSeconds() {
        return scanSleepSeconds;
    }

    public void setScanSleepSeconds(int scanSleepSeconds) {
        this.scanSleepSeconds = scanSleepSeconds;
    }

    public String getPdbLoginUrl() {
        return pdbLoginUrl;
    }

    public void setPdbLoginUrl(String pdbLoginUrl) {
        this.pdbLoginUrl = pdbLoginUrl;
    }

    public String getMdbConnecCodeName() {
        return mdbConnecCodeName;
    }

    public void setMdbConnecCodeName(String mdbConnecCodeName) {
        this.mdbConnecCodeName = mdbConnecCodeName;
    }

    public int getParamLoadMdbRouteType() {
        return paramLoadMdbRouteType;
    }

    public void setParamLoadMdbRouteType(int paramLoadMdbRouteType) {
        this.paramLoadMdbRouteType = paramLoadMdbRouteType;
    }
}
