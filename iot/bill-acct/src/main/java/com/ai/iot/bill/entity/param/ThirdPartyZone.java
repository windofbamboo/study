package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**第三方计费 TD_B_THIRD_PARTY_ZONE
 * Created by geyunfeng on 2017/8/4.
 */
public class ThirdPartyZone implements Serializable, IdInterface, Comparable<ThirdPartyZone> {

  private static final long serialVersionUID = 3378181633709422512L;
  private int planVersionId;
  private int zoneId;

  public ThirdPartyZone() {
  }

  @Override
  public int compareTo(ThirdPartyZone o) {
    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;

    if (this.zoneId < o.zoneId) return -1;
    if (this.zoneId > o.zoneId) return 1;

    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ThirdPartyZone that = (ThirdPartyZone) o;

    if (planVersionId != that.planVersionId) return false;
    return zoneId == that.zoneId;

  }

  @Override
  public int hashCode() {
    int result = planVersionId;
    result = 31 * result + zoneId;
    return result;
  }

  @Override
  public String toString() {
    return "ThirdPartyZone{" +
            "planVersionId=" + planVersionId +
            ", zoneId=" + zoneId +
            '}';
  }

  @Override
  public int getId() {
    return planVersionId;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

}
