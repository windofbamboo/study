package com.ai.iot.bill.common.paramcachemanager.core.pobase;

import java.util.LinkedList;

import net.sf.ehcache.Element;

/**ehcache3不支持泛型,所以这里简单的包了一个*/
public class PoBaseLinkedList extends LinkedList<PoBase> {
	private static final long serialVersionUID = -750619547896143921L;
	
	///仅仅为ehcache2准备
	private Element element=null;
	
	public PoBaseLinkedList() {
		element=null;
	}

	///仅仅为ehcache2准备
	public void setElement(String poKey) {
		this.element = new Element(poKey,this);
	}

	///仅仅为ehcache2准备
	public Element getElement() {
		return element;
	}
	
//	public PoBaseLinkedList getElementObject() {
//		if(CheckNull.isNull(element)) {
//			return null;
//		}
//		return (PoBaseLinkedList) element.getObjectValue();
//	}
}
