package com.ai.iot.mdb.common.rate;

import com.ai.iot.bill.common.cdr.Cdr;
import com.ai.iot.bill.common.cdr.CdrAttri;
import com.ai.iot.bill.common.util.HashUtil;

public class RatingHash {

	public static String getKey(Cdr cdr) {
		return "500" + HashUtil.ELFHashMod(cdr.get(CdrAttri.ATTRI_GUID));
	}
}
