package com.ai.iot.mdb.common.mdb.mog;

import com.ai.iot.mdb.common.device.*;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import com.ai.iot.mdb.common.mdb.MoGroup;

public class DeviceInfoMoGroup extends MoGroup {
    public DeviceInfoMoGroup() {
        super.name = MoBaseRegisterFactory.MoGroupEnum.MOG_DEVINFO.toString();
        MoBase moBase = new DeviceMo();
        moBases.put(moBase.getName(), moBase);

        moBase = new DeviceRatePlanMo();
        moBases.put(moBase.getName(), moBase);
        moBase = new DeviceOthersMo();
        moBases.put(moBase.getName(), moBase);
        moBase = new RenewalRateQueueMo();
        moBases.put(moBase.getName(), moBase);
        moBase = new DeviceMsisdnMo();
        moBases.put(moBase.getName(), moBase);
        moBase = new DeviceImsiMo();
        moBases.put(moBase.getName(), moBase);

        moBase = new AccountMo();
        moBases.put(moBase.getName(), moBase);
        moBase = new AccountAppMo();
        moBases.put(moBase.getName(), moBase);
    }
}
