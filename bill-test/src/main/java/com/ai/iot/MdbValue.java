package com.ai.iot;

import com.ai.iot.mdb.common.rate.MdbBillPoolSum2Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Gsm;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Sms;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Gsm;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Sms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyunfeng on 2017/10/9.
 */
public class MdbValue {

  private List<MdbBillUserSum1Gprs> mdbBillUserSum1GprsList = new ArrayList<>();
  private List<MdbBillUserSum2Gprs> mdbBillUserSum2GprsList = new ArrayList<>();
  private List<MdbBillUserSum1Gsm> mdbBillUserSum1GsmList = new ArrayList<>();
  private List<MdbBillUserSum2Gsm> mdbBillUserSum2GsmList = new ArrayList<>();
  private List<MdbBillUserSum1Sms> mdbBillUserSum1SmsList = new ArrayList<>();
  private List<MdbBillUserSum2Sms> mdbBillUserSum2SmsList = new ArrayList<>();
  private List<MdbBillPoolSum2Gprs> mdbBillPoolSum2GprsList = new ArrayList<>();

  public List<MdbBillUserSum1Gprs> getMdbBillUserSum1GprsList() {
    return mdbBillUserSum1GprsList;
  }

  public List<MdbBillUserSum2Gprs> getMdbBillUserSum2GprsList() {
    return mdbBillUserSum2GprsList;
  }

  public List<MdbBillUserSum1Gsm> getMdbBillUserSum1GsmList() {
    return mdbBillUserSum1GsmList;
  }

  public List<MdbBillUserSum2Gsm> getMdbBillUserSum2GsmList() {
    return mdbBillUserSum2GsmList;
  }

  public List<MdbBillUserSum1Sms> getMdbBillUserSum1SmsList() {
    return mdbBillUserSum1SmsList;
  }

  public List<MdbBillUserSum2Sms> getMdbBillUserSum2SmsList() {
    return mdbBillUserSum2SmsList;
  }

  public List<MdbBillPoolSum2Gprs> getMdbBillPoolSum2GprsList() {
    return mdbBillPoolSum2GprsList;
  }


}
