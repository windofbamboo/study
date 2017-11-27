package com.ai.iot.bill.common.param;

import com.ai.iot.bill.common.util.IdInterface;

public class RouteBean implements IdInterface {

	int routeType;
	int provinceCode;
	int computerMode;
	long min;
	long max;
	int sourceId;

	public RouteBean() {
	}

	public RouteBean(int routeType, int provinceCode, int computerMode, long min, long max, int sourceId) {
		this.routeType = routeType;
		this.provinceCode = provinceCode;
		this.computerMode = computerMode;
		this.min = min;
		this.max = max;
		this.sourceId = sourceId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RouteBean routeBean = (RouteBean) o;

		if (routeType != routeBean.routeType) return false;
		if (provinceCode != routeBean.provinceCode) return false;
		return computerMode == routeBean.computerMode;

	}

	@Override
	public int hashCode() {
		int result = routeType;
		result = 31 * result + provinceCode;
		result = 31 * result + computerMode;
		return result;
	}

	@Override
	public String toString() {
		return "RouteBean{" +
				"routeType=" + routeType +
				", provinceCode=" + provinceCode +
				", computerMode=" + computerMode +
				", min=" + min +
				", max=" + max +
				", sourceId=" + sourceId +
				'}';
	}

	public int getId() {
		return routeType;
	}

	public int getRouteType() {
		return routeType;
	}

	public void setRouteType(int routeType) {
		this.routeType = routeType;
	}

	public int getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(int provinceCode) {
		this.provinceCode = provinceCode;
	}

	public int getComputerMode() {
		return computerMode;
	}

	public void setComputerMode(int computerMode) {
		this.computerMode = computerMode;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	
	
}
