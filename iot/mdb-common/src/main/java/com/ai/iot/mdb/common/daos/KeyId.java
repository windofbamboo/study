package com.ai.iot.mdb.common.daos;

public class KeyId {
    private Long keyId;

    public Long getKeyId() {
        return keyId;
    }

    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = Long.valueOf(keyId);
    }

    public String toString() {
        return String.valueOf(keyId);
    }

    public long getlong() {
        return keyId.longValue();
    }

    public void count() {
        keyId++;
    }
}