package com.ai.iot.bill.common.mdb;

import java.util.ArrayList;
import java.util.List;

public class MoContainer {
    private List<MoBase> moBases;
    private MoBase moBase;
    public MoContainer(MoBase moBase) {
        this.moBase = moBase;
        moBases = new ArrayList<>();
    }

    public MoContainer(MoBase moBase, List<MoBase> moBases) {
        this.moBase = moBase;
        this.moBases = moBases;
    }

    public void add(MoBase moBase) {
        moBases.add(moBase);
    }

    public List<MoBase> getMdbBases() {
        return moBases;
    }

    public String getName() {
        return moBase.getName();
    }

    public boolean isEmpty() {
        return moBases.isEmpty();
    }
}
