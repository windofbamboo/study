package com.ai.iot.bill.cb.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImeiChangedNotify extends BaseNotify {
    private static final Logger logger = LoggerFactory.getLogger(ImeiChangedNotify.class);
    private static final long serialVersionUID = 2017091410380957001L;
    public final static String name = "OTHER-04";

    @JSONField(name = "ACCOUNT_ID")
    private Long acctId;

    @JSONField(name = "DEVICE_ID")
    private Long deviceId;

    @JSONField(name = "NEW_IMEI")
    private String newImei;

    @JSONField(name = "OLD_IMEI")
    private String oldImei;

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getNewImei() {
        return newImei;
    }

    public void setNewImei(String newImei) {
        this.newImei = newImei;
    }

    public String getOldImei() {
        return oldImei;
    }

    public void setOldImei(String oldImei) {
        this.oldImei = oldImei;
    }
}
