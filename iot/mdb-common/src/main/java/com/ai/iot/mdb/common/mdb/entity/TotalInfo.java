package com.ai.iot.mdb.common.mdb.entity;

public class TotalInfo {
    //总记录条数
    private Long totalNum;
    //最小的ID
    private Long minId;
    //最大的ID
    private Long maxId;

    public Long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public Long getMinId() {
        return minId;
    }

    public void setMinId(Long minId) {
        this.minId = minId;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }
}
