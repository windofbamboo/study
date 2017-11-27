package com.ai.iot.mdb.common.session.mos;

import java.util.List;

public class MoModifyKey {
	private String mmName;
	private List<String> keys;
	public String getMmName() {
		return mmName;
	}
	public void setMmName(String mmName) {
		this.mmName = mmName;
	}
	public List<String> getKeys() {
		return keys;
	}
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	
}
