package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**
 * Created by geyunfeng on 2017/7/28.
 */
public class Opn implements Serializable {

  private static final long serialVersionUID = -6639726168800925045L;
  private String opnCode;
  private long price;

  public Opn() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Opn opn = (Opn) o;

    return opnCode != null ? opnCode.equals(opn.opnCode) : opn.opnCode == null;

  }

  @Override
  public int hashCode() {
    return opnCode != null ? opnCode.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Opn{" +
            "opnCode='" + opnCode + '\'' +
            ", price=" + price +
            '}';
  }

  public String getOpnCode() {
    return opnCode;
  }

  public void setOpnCode(String opnCode) {
    this.opnCode = opnCode;
  }

  public long getPrice() {
    return price;
  }

  public void setPrice(long price) {
    this.price = price;
  }
}
