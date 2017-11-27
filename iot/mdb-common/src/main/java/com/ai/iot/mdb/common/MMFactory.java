package com.ai.iot.mdb.common;

import com.ai.iot.bill.common.mdb.MMBase;
import com.ai.iot.bill.common.mdb.MdbCommonException;
import com.ai.iot.mdb.common.autorule.MMAutorule;
import com.ai.iot.mdb.common.device.MMDevice;

public class MMFactory {
	
	public static enum MMFactoryEnum{
		MM_DEVINFO,
		MM_RATING,
		MM_AUTORULE,
		MM_DUPCHECK,
		MM_RATINGCDR,
		MM_SESSION
	}
	
	public MMBase newMMObject(MMFactoryEnum mmFactoryEnum,boolean isMdbMaster) throws MdbCommonException {
		if(mmFactoryEnum==MMFactoryEnum.MM_AUTORULE) {
			return new MMAutorule(isMdbMaster);
		}else if(mmFactoryEnum==MMFactoryEnum.MM_DEVINFO) {
			return new MMDevice(isMdbMaster);
		}else if(mmFactoryEnum==MMFactoryEnum.MM_RATING) {
            //return new BillManager();
        }
		throw new MdbCommonException(MdbCommonException.MdbCommonExceptionENUM.MM_OBJECT_IS_NULL);
	}
}
