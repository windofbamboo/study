package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;

public class MtDeviceMsisdn extends MdbTable {
    private String msisdn;

    public MtDeviceMsisdn() {
        addMoBaseField(DeviceMsisdnMo.class);
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public String getMdbTableKeyId() {
        return MoBaseRegisterFactory.mdbOperaCode_MSISDN;
    }

    @Override
    public String getKey() {
        StringBuffer key = new StringBuffer();
        key.append(MoBaseRegisterFactory.mdbOperaCode_MSISDN);
        key.append(MoBaseRegisterFactory.separator);
        key.append(msisdn);
        return key.toString();
    }
}
