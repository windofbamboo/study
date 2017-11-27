package com.ai.iot.bill.common.util;

import java.util.List;

public class ProtostuffList{
	@SuppressWarnings("rawtypes")
	List list;
	
	public ProtostuffList(){}	

    @SuppressWarnings("rawtypes")
	public List getList() {
        return list;
    }
    
    @SuppressWarnings("rawtypes")
	public void setList(List list) {
        this.list = list;
    }
}
