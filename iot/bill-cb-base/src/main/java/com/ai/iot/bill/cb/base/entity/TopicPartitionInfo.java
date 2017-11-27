package com.ai.iot.bill.cb.base.entity;

/**
 * KAFKA主题和分区信息类
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/12 09:36]
 * @Version:      [v1.0]
 */
public class TopicPartitionInfo {
    private String topic;
    private int partitionNum = 1;
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }
}
