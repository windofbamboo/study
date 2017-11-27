package com.ai.iot.mdb.common.mdb.mog;

import com.ai.iot.mdb.common.device.*;

import java.util.List;

public class DevInfoMoSet {
    private DeviceMo deviceMo;
    private List<DeviceRatePlanMo> deviceRatePlanMos;
    private DeviceMsisdnMo deviceMsisdnMo;
    private DeviceImsiMo deviceImsiMo;
    private AccountMo accountMo;
    private AccountAppMo accountAppMo;

    public DeviceMo getDeviceMo() {
        return deviceMo;
    }

    public void setDeviceMo(DeviceMo deviceMo) {
        this.deviceMo = deviceMo;
    }

    public List<DeviceRatePlanMo> getDeviceRatePlanMos() {
        return deviceRatePlanMos;
    }

    public void setDeviceRatePlanMos(List<DeviceRatePlanMo> deviceRatePlanMos) {
        this.deviceRatePlanMos = deviceRatePlanMos;
    }

    public DeviceMsisdnMo getDeviceMsisdnMo() {
        return deviceMsisdnMo;
    }

    public void setDeviceMsisdnMo(DeviceMsisdnMo deviceMsisdnMo) {
        this.deviceMsisdnMo = deviceMsisdnMo;
    }

    public DeviceImsiMo getDeviceImsiMo() {
        return deviceImsiMo;
    }

    public void setDeviceImsiMo(DeviceImsiMo deviceImsiMo) {
        this.deviceImsiMo = deviceImsiMo;
    }

    public AccountMo getAccountMo() {
        return accountMo;
    }

    public void setAccountMo(AccountMo accountMo) {
        this.accountMo = accountMo;
    }

    public AccountAppMo getAccountAppMo() {
        return accountAppMo;
    }

    public void setAccountAppMo(AccountAppMo accountAppMo) {
        this.accountAppMo = accountAppMo;
    }
}
