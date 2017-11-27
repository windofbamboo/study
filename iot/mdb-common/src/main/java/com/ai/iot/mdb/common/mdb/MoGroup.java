package com.ai.iot.mdb.common.mdb;

import com.ai.iot.bill.common.mdb.MoBase;

import java.util.HashMap;
import java.util.Map;

public class MoGroup {
    protected String name;
    protected Map<String, MoBase> moBases;

    public MoGroup() {
        moBases = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, MoBase> getMoBases() {
        return moBases;
    }
}
