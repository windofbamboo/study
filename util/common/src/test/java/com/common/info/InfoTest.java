package com.common.info;

import java.sql.Date;

public class InfoTest {

    private long id;
    private String propertyValue;
    private Date updateTime;

    public InfoTest() {
    }

    public InfoTest(long id, String propertyValue) {
        this.id = id;
        this.propertyValue = propertyValue;
    }

    public InfoTest(long id, String propertyValue, Date updateTime) {
        this.id = id;
        this.propertyValue = propertyValue;
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfoTest infoTest = (InfoTest) o;

        if (id != infoTest.id) return false;
        if (propertyValue != null ? !propertyValue.equals(infoTest.propertyValue) : infoTest.propertyValue != null)
            return false;
        return updateTime != null ? updateTime.equals(infoTest.updateTime) : infoTest.updateTime == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (propertyValue != null ? propertyValue.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
    	return "InfoTest{" +
                "id=" + id +
                ", propertyValue='" + propertyValue + '\'' +
                ", updateTime=" + updateTime.toString() +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
