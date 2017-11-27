package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**账目表
 * Created by geyunfeng on 2017/6/6.
 */
public class ItemBean implements Serializable {

  private static final long serialVersionUID = -8581122577977613747L;
  private int itemId;
  private int itemClass;
  private int provItemId;
  private int headquarterItemId;
  private int billType;

  public ItemBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemBean itemBean = (ItemBean) o;

    return itemId == itemBean.itemId;

  }

  @Override
  public int hashCode() {
    return itemId;
  }

  @Override
  public String toString() {
    return "ItemBean{" +
            "itemId=" + itemId +
            ", itemClass=" + itemClass +
            ", provItemId=" + provItemId +
            ", headquarterItemId=" + headquarterItemId +
            ", billType=" + billType +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getItemClass() {
    return itemClass;
  }

  public void setItemClass(int itemClass) {
    this.itemClass = itemClass;
  }

  public int getProvItemId() {
    return provItemId;
  }

  public void setProvItemId(int provItemId) {
    this.provItemId = provItemId;
  }

  public int getHeadquarterItemId() {
    return headquarterItemId;
  }

  public void setHeadquarterItemId(int headquarterItemId) {
    this.headquarterItemId = headquarterItemId;
  }

  public int getBillType() {
    return billType;
  }

  public void setBillType(int billType) {
    this.billType = billType;
  }
}
