package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**第三方计费 TD_B_THIRD_PARTY_VERSION
 * Created by geyunfeng on 2017/8/4.
 */
public class ThirdPartyVersion implements Serializable, CycleInterface, Comparable<ThirdPartyVersion> {

  private static final long serialVersionUID = -5499238949823365598L;
  private int planId;
  private int planVersionId;
  private int startCycleId;
  private int endCycleId;
  private int levelModel;

  public ThirdPartyVersion() {
  }

  @Override
  public int compareTo(ThirdPartyVersion o) {
    if (this.planId < o.planId) return -1;
    if (this.planId > o.planId) return 1;

    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;

    if (this.startCycleId < o.startCycleId) return -1;
    if (this.startCycleId > o.startCycleId) return 1;

    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ThirdPartyVersion that = (ThirdPartyVersion) o;

    if (planId != that.planId) return false;
    if (planVersionId != that.planVersionId) return false;
    return startCycleId == that.startCycleId;

  }

  @Override
  public int hashCode() {
    int result = planId;
    result = 31 * result + planVersionId;
    result = 31 * result + startCycleId;
    return result;
  }

  @Override
  public String toString() {
    return "ThirdPartyVersion{" +
            "planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", startCycleId=" + startCycleId +
            ", endCycleId=" + endCycleId +
            ", levelModel=" + levelModel +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int planId) {
    this.planId = planId;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  @Override
  public int getStartCycleId() {
    return startCycleId;
  }

  public void setStartCycleId(int startCycleId) {
    this.startCycleId = startCycleId;
  }

  @Override
  public int getEndCycleId() {
    return endCycleId;
  }

  public void setEndCycleId(int endCycleId) {
    this.endCycleId = endCycleId;
  }

  public int getLevelModel() {
    return levelModel;
  }

  public void setLevelModel(int levelModel) {
    this.levelModel = levelModel;
  }

}
