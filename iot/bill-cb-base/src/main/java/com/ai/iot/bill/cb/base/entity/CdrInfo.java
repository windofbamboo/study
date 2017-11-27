package com.ai.iot.bill.cb.base.entity;

import com.ai.iot.bill.common.cdr.Cdr;
import com.ai.iot.bill.common.cdr.CdrAttri;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.mdb.common.device.DeviceRatePlanMo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 话单对象的包装类， 包装在Cdr对象外的一层，用以扩展，可以承载更多的属性和信息。
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/12 09:36]
 * @Version:      [v1.0]
 */
public class CdrInfo {
    private final Logger logger = LoggerFactory.getLogger(CdrInfo.class);
    //如果分拣内部使用，cdr对象可以使用
    //如果传给外部模块，如rate，cdr对象会出问题，因为跨线程且cdr中含有map。在获取cdr的时候需要临时从cdrstr转化成cdr对象。
    //emitState就表示cdr 的getCdr() 方法是直接返回cdr，还是从cdrstr转化成cdr来返回。
    //false:直接返回cdr，true:把cdrstr转化成cdr来返回。
    private boolean emitState = false;
    private String cdrstr;
    private Cdr cdr;
    private List<DeviceRatePlanMo> deviceRatePlanMoList;
    //根据IMSI求取出来的imei，与话单自带的IMEI可能不同，当不同时，需要给CRM发送消息。
    private String imei;
    private int sendTimes = 0;
    private String dispId;
    private boolean recvConfirm = false;

    public CdrInfo() {
    }

    public boolean isEmitState() {
        return emitState;
    }

    public void setEmitState(boolean emitState) {
        if (CheckNull.isNull(cdrstr) && !CheckNull.isNull(cdr)) {
            cdrstr = cdr.toIndexString();
        }
        this.emitState = emitState;
    }

    public void reset() {
        sendTimes = 0;
        recvConfirm = false;
        dispId = null;
        imei = null;
        deviceRatePlanMoList = null;
        cdrstr = null;
        cdr = null;
    }

    public void setCdr(Cdr cdr) {
        this.cdr = cdr;
    }

    public Cdr getCdr() {
        if (emitState) {
            cdr.fromIndexString(cdrstr);
            return cdr;
        }

        return cdr;
    }

    public String getCdrstr() {
        if (CheckNull.isNull(cdrstr) && !CheckNull.isNull(cdr)) {
            cdrstr = cdr.toIndexString();
        }
        return cdrstr;
    }

    public void setCdrstr(String cdrstr) {
        this.cdrstr = cdrstr;
    }

    public List<DeviceRatePlanMo> getDeviceRatePlanMoList() {
        return deviceRatePlanMoList;
    }

    public void setDeviceRatePlanMoList(List<DeviceRatePlanMo> deviceRatePlanMoList) {
        this.deviceRatePlanMoList = deviceRatePlanMoList;
    }

    public int getSendTimes() {
        return sendTimes;
    }

    public void setSendTimes(int sendTimes) {
        this.sendTimes = sendTimes;
    }

    public String getDispId() {
        return dispId;
    }

    public void setDispId(String dispId) {
        this.dispId = dispId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isRecvConfirm() {
        return recvConfirm;
    }

    public void setRecvConfirm(boolean recvConfirm) {
        this.recvConfirm = recvConfirm;
    }

    public boolean isImeiChanged(Cdr cdr) {
        logger.info("#########isImeiChanged() executed. cdr's imei:{}, calc's imei:{}##########", cdr.get(CdrAttri.ATTRI_IMEI), imei);
        if (CheckNull.isNull(imei)) {
            logger.info("#########isImeiChanged() executed. calc's imei is null.##########");
            return false;
        }

        if (CheckNull.isNull(cdr.get(CdrAttri.ATTRI_IMEI))) {
            logger.info("#########isImeiChanged() executed. cdr's imei is null.##########");
            return false;
        }

        if (!imei.equals(cdr.get(CdrAttri.ATTRI_IMEI))) {
            logger.info("#########isImeiChanged() IMEI CHANGED.... cdr's imei:{}, calc's imei:{}##########", cdr.get(CdrAttri.ATTRI_IMEI), imei);
            return true;
        }

        return false;
    }
}
