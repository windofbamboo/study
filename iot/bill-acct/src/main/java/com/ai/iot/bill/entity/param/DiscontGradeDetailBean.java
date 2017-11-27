package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;
import com.ai.iot.bill.entity.info.TimeInterface;

import java.io.Serializable;
import java.sql.Date;

/**TD_B_DISCONT_GRADE_DETAIL
 * Created by geyunfeng on 2017/7/28.
 */
public class DiscontGradeDetailBean implements Serializable, IdInterface, TimeInterface, Comparable<DiscontGradeDetailBean> {

  private static final long serialVersionUID = 4420056955606066020L;
  private int gradeId;
  private int gradeIndex;
  private int deviceUp;
  private int deviceLower;
  private double discount;
  private Date startTime;
  private Date endTime;

  public DiscontGradeDetailBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DiscontGradeDetailBean that = (DiscontGradeDetailBean) o;

    if (gradeId != that.gradeId) return false;
    return gradeIndex == that.gradeIndex;

  }

  @Override
  public int hashCode() {
    int result = gradeId;
    result = 31 * result + gradeIndex;
    return result;
  }

  public int compareTo(DiscontGradeDetailBean o) {
    if (this.gradeId < o.gradeId) return -1;
    if (this.gradeId > o.gradeId) return 1;

    if (this.gradeIndex < o.gradeIndex) return -1;
    if (this.gradeIndex > o.gradeIndex) return 1;
    return 0;
  }


  @Override
  public String toString() {
    return "DiscontGradeDetailBean{" +
            "gradeId=" + gradeId +
            ", gradeIndex=" + gradeIndex +
            ", deviceUp=" + deviceUp +
            ", deviceLower=" + deviceLower +
            ", discount=" + discount +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
  }

  public int getId() {
    return gradeId;
  }


  public int getGradeId() {
    return gradeId;
  }

  public void setGradeId(int gradeId) {
    this.gradeId = gradeId;
  }

  public int getGradeIndex() {
    return gradeIndex;
  }

  public void setGradeIndex(int gradeIndex) {
    this.gradeIndex = gradeIndex;
  }

  public int getDeviceUp() {
    return deviceUp;
  }

  public void setDeviceUp(int deviceUp) {
    this.deviceUp = deviceUp;
  }

  public int getDeviceLower() {
    return deviceLower;
  }

  public void setDeviceLower(int deviceLower) {
    this.deviceLower = deviceLower;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  @Override
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @Override
  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
