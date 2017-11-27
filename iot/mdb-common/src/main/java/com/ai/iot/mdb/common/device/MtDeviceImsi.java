package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;

public class MtDeviceImsi extends MdbTable {
    private String imsi;

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public MtDeviceImsi() {
        addMoBaseField(DeviceImsiMo.class);
    }

    @Override
    public String getMdbTableKeyId() {
        return MoBaseRegisterFactory.mdbOperaCode_IMSI;
    }

    @Override
    public String getKey() {
        StringBuffer key = new StringBuffer();
        key.append(MoBaseRegisterFactory.mdbOperaCode_IMSI);
        key.append(MoBaseRegisterFactory.separator);
        key.append(imsi);
        return key.toString();
    }
}
