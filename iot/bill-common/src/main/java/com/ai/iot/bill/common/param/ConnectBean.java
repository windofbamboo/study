package com.ai.iot.bill.common.param;

import com.ai.iot.bill.common.util.IdInterface;

public class ConnectBean implements IdInterface {

    private int connId;
    private String connCode;
    private String connectStr;
    private String pdbUser;
    private String passWord;

    public ConnectBean() {
    }

    public ConnectBean(int connId, String connCode, String connectStr, String pdbUser, String passWord) {
        this.connId = connId;
        this.connCode = connCode;
        this.connectStr = connectStr;
        this.pdbUser = pdbUser;
        this.passWord = passWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectBean that = (ConnectBean) o;

        return connId == that.connId;

    }

    @Override
    public int hashCode() {
        return connId;
    }

    @Override
    public String toString() {
        return "ConnectBean{" +
                "connId=" + connId +
                ", connCode='" + connCode + '\'' +
                ", connectStr='" + connectStr + '\'' +
                ", pdbUser='" + pdbUser + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }

    public int getId() {
        return connId;
    }

    public int getConnId() {
        return connId;
    }

    public void setConnId(int connId) {
        this.connId = connId;
    }

    public String getConnCode() {
        return connCode;
    }

    public void setConnCode(String connCode) {
        this.connCode = connCode;
    }

    public String getConnectStr() {
        return connectStr;
    }

    public void setConnectStr(String connectStr) {
        this.connectStr = connectStr;
    }

    public String getPdbUser() {
        return pdbUser;
    }

    public void setPdbUser(String pdbUser) {
        this.pdbUser = pdbUser;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
