/**
 * 
 */
package com.ai.iot.bill.common.param;

/**
 * @author XLR
 *
 */
public class CycleBean {
	private String cycleID;
	private String cycStartTime;
	private String cycEndTime;
	private String useTag;
	private String addTag;
	private String cycHelfTime;

	public CycleBean() {
	}

	public CycleBean(String cycleID, String cycStartTime, String cycEndTime, String useTag, String addTag, String cycHelfTime) {
		this.cycleID = cycleID;
		this.cycStartTime = cycStartTime;
		this.cycEndTime = cycEndTime;
		this.useTag = useTag;
		this.addTag = addTag;
		this.cycHelfTime = cycHelfTime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CycleBean cycleBean = (CycleBean) o;

		return cycleID != null ? cycleID.equals(cycleBean.cycleID) : cycleBean.cycleID == null;

	}

	@Override
	public int hashCode() {
		return cycleID != null ? cycleID.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "CycleBean{" +
				"cycleID='" + cycleID + '\'' +
				", cycStartTime='" + cycStartTime + '\'' +
				", cycEndTime='" + cycEndTime + '\'' +
				", useTag='" + useTag + '\'' +
				", addTag='" + addTag + '\'' +
				", cycHelfTime='" + cycHelfTime + '\'' +
				'}';
	}

	public String getCycleID() {
		return cycleID;
	}

	public void setCycleID(String cycleID) {
		this.cycleID = cycleID;
	}

	public String getCycStartTime() {
		return cycStartTime;
	}

	public void setCycStartTime(String cycStartTime) {
		this.cycStartTime = cycStartTime;
	}

	public String getCycEndTime() {
		return cycEndTime;
	}

	public void setCycEndTime(String cycEndTime) {
		this.cycEndTime = cycEndTime;
	}

	public String getUseTag() {
		return useTag;
	}

	public void setUseTag(String useTag) {
		this.useTag = useTag;
	}

	public String getAddTag() {
		return addTag;
	}

	public void setAddTag(String addTag) {
		this.addTag = addTag;
	}

	public String getCycHelfTime() {
		return cycHelfTime;
	}

	public void setCycHelfTime(String cycHelfTime) {
		this.cycHelfTime = cycHelfTime;
	}
}
