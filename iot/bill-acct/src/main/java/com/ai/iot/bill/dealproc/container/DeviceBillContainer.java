package com.ai.iot.bill.dealproc.container;

import com.ai.iot.bill.entity.computebill.DeviceBillActivation;
import com.ai.iot.bill.entity.computebill.DeviceBillOrder;
import com.ai.iot.bill.entity.computebill.DeviceBillUsage;
import com.ai.iot.bill.entity.multibill.DeviceBill;
import com.ai.iot.bill.entity.multibill.DeviceBillActive;
import com.ai.iot.bill.entity.multibill.DeviceBillData;
import com.ai.iot.bill.entity.multibill.DeviceBillPrepay;
import com.ai.iot.bill.entity.multibill.DeviceBillSms;
import com.ai.iot.bill.entity.multibill.DeviceBillVoice;
import com.ai.iot.bill.entity.multibill.DeviceUsage;
import com.ai.iot.bill.entity.res.ResIncludeDevice;
import com.ai.iot.bill.entity.res.ResIncludePile;
import com.ai.iot.bill.entity.res.ResIncludeShareTurn;
import com.ai.iot.bill.entity.res.ResUsedDevice;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import com.ai.iot.bill.entity.usage.UsedAddShareDetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在设备级用量核减过程中，需要使用到的信息
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class DeviceBillContainer implements Serializable {

  private static final long serialVersionUID = -3965385821732221200L;

  //用量部分
  private List<ResIncludeDevice> resIncludeDeviceList = new ArrayList<>(); //设备的资费计划中,包含的用量(个人部分的)
  private List<ResIncludeShareTurn> resIncludeAgileShareTrunList = new ArrayList<>(); //灵活共享资费计划，包含的用量转移信息(含自身、含堆叠)
  private List<ResIncludeShareTurn> resIncludeFixShareTurnList = new ArrayList<>(); //固定共享资费计划，包含的用量转移信息(含自身、堆叠)
  private List<ResIncludePile> resIncludePileList = new ArrayList<>(); //堆叠资费的轨迹

  //累积量信息
  private List<UsedAddDevice> usedAddDeviceList = new ArrayList<>(); //设备的使用量信息(累积量信息)

  //个人核减信息
  private List<ResUsedDevice> resUsedDeviceList = new ArrayList<>(); //设备的用量核减信息(个人部分的)

  //此数据需要放在内存库中，作为中间量
  private List<UsedAddShareDetail> usedAddShareDetailList = new ArrayList<>(); //设备的使用量信息(累积量信息)--用于共享部分

  //费用部分
  private List<DeviceBillUsage> deviceBillUsageList = new ArrayList<>(); //用量超额费用
  private List<DeviceBillOrder> deviceBillOrderList = new ArrayList<>(); //资费计划的费用
  private List<DeviceBillActivation> deviceBillActivationList = new ArrayList<>();//激活账单
  private long acctId = 0L;

  //多维度账单部分
  private DeviceUsage deviceUsage = new DeviceUsage();
  private List<DeviceBillActive> deviceBillActiveList = new ArrayList<>();
  private List<DeviceBillData> deviceBillDataList = new ArrayList<>();
  private List<DeviceBillSms> deviceBillSmsList = new ArrayList<>();
  private List<DeviceBillVoice> deviceBillVoiceList = new ArrayList<>();
  private List<DeviceBillPrepay> deviceBillPrepayList = new ArrayList<>();
  private DeviceBill deviceBill = new DeviceBill();

  public DeviceBillContainer() {
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public List<ResIncludeDevice> getResIncludeDeviceList() {
    return resIncludeDeviceList;
  }

  public ResIncludeDevice getResIncludeDevice(long tpInsId, int billId) {
    ResIncludeDevice resIncludeDevice = new ResIncludeDevice();
    resIncludeDevice.setTpInsId(tpInsId);
    resIncludeDevice.setBillId(billId);

    int a = resIncludeDeviceList.indexOf(resIncludeDevice);
    if (a == -1)
      return null;
    return resIncludeDeviceList.get(a);
  }

  public List<ResIncludeDevice> getResIncludeDeviceList(long tpInsId) {

    if (this.resIncludeDeviceList == null || this.resIncludeDeviceList.isEmpty()) {
      return Collections.emptyList();
    }
    return resIncludeDeviceList.stream().filter(t -> t.getTpInsId() == tpInsId).collect(Collectors.toList());

  }


  public void addResIncludeDeviceList(ResIncludeDevice resIncludeDevice) {
    this.resIncludeDeviceList.add(resIncludeDevice);
  }

  public List<ResIncludeShareTurn> getResIncludeAgileShareTrunList() {
    return resIncludeAgileShareTrunList;
  }

  public List<ResIncludeShareTurn> getResIncludeAgileShareTrunList(long tpInsId) {
    if (this.resIncludeAgileShareTrunList == null || this.resIncludeAgileShareTrunList.isEmpty()) {
      return Collections.emptyList();
    }
    return resIncludeAgileShareTrunList.stream().filter(t -> t.getTpInsId() == tpInsId).collect(Collectors.toList());
  }

  public ResIncludeShareTurn getResIncludeAgileShareTrun(long tpInsId,int billId) {
    if (resIncludeAgileShareTrunList == null || resIncludeAgileShareTrunList.isEmpty()) {
      return null;
    }
    return resIncludeAgileShareTrunList.stream()
        .filter(t -> t.getTpInsId() == tpInsId && t.getBillId() == billId).findAny().orElse(null);
  }

  public void addResIncludeAgileShareTrunList(ResIncludeShareTurn resIncludeAgileShareTrun) {
    this.resIncludeAgileShareTrunList.add(resIncludeAgileShareTrun);
  }

  public List<ResIncludeShareTurn> getResIncludeFixShareTurnList() {
    return resIncludeFixShareTurnList;
  }

  public ResIncludeShareTurn getResIncludeFixShareTurn(long tpInsId, int billId) {
    if (resIncludeFixShareTurnList == null || resIncludeFixShareTurnList.isEmpty()) {
      return null;
    }
    return resIncludeFixShareTurnList.stream()
           .filter(t -> t.getTpInsId() == tpInsId && t.getBillId() == billId).findAny().orElse(null);
  }

  public void addResIncludeFixShareTurnList(ResIncludeShareTurn resIncludeShareTurn) {
    this.resIncludeFixShareTurnList.add(resIncludeShareTurn);
  }

  public List<ResIncludePile> getResIncludePileList() {
    return resIncludePileList;
  }

  public List<ResIncludePile> getResIncludePileList(long tpInsId){
    if (this.resIncludePileList == null || this.resIncludePileList.isEmpty()) {
      return null;
    }
    return resIncludePileList.stream().filter(t -> t.getTpInsId() == tpInsId).collect(Collectors.toList());
  }

  public void addResIncludePileList(ResIncludePile resIncludePile) {
    this.resIncludePileList.add(resIncludePile);
  }


  public List<UsedAddShareDetail> getUsedAddShareDetailList() {
    return usedAddShareDetailList;
  }

  public void addUsedAddShareDetailList(UsedAddShareDetail usedAddShareDetail) {
    this.usedAddShareDetailList.add(usedAddShareDetail);
  }


  public List<UsedAddDevice> getUsedAddDeviceList() {
    return usedAddDeviceList;
  }

  public UsedAddDevice getUsedAddDevice(long tpInsId, int billId) {

    if (this.usedAddDeviceList == null || this.usedAddDeviceList.isEmpty()) {
      return null;
    }
    return usedAddDeviceList.stream()
        .filter(t -> t.getTpInsId() == tpInsId && t.getBillId() == billId).findAny().orElse(null);
  }

  public List<UsedAddDevice> getUsedAddDeviceList(long tpInsId) {

    if (this.usedAddDeviceList == null || this.usedAddDeviceList.isEmpty()) {
      return Collections.emptyList();
    }
    return usedAddDeviceList.stream()
        .filter(t -> t.getTpInsId() == tpInsId ).collect(Collectors.toList());
  }


  public void setUsedAddDeviceList(List<UsedAddDevice> usedAddDeviceList) {
    this.usedAddDeviceList = usedAddDeviceList;
  }


  public List<ResUsedDevice> getResUsedDeviceList() {
    return resUsedDeviceList;
  }

  public void addResUsedDeviceList(ResUsedDevice resUsedDevice) {
    this.resUsedDeviceList.add(resUsedDevice);
  }

  public List<DeviceBillUsage> getDeviceBillUsageList() {
    return deviceBillUsageList;
  }

  public long getUsageFee(long tpInsId, int planVersionId, int billId) {

    if (deviceBillUsageList == null || deviceBillUsageList.isEmpty()){
      return 0;
    }
    return deviceBillUsageList.stream()
            .filter(t -> t.getTpInsId() == tpInsId &&
                         t.getPlanVersionId() == planVersionId &&
                         t.getBillId() == billId ).mapToLong(DeviceBillUsage::getFee).sum();
  }


  public void addDeviceBillUsageList(DeviceBillUsage deviceBillUsage) {
    this.deviceBillUsageList.add(deviceBillUsage);
  }

  public List<DeviceBillOrder> getDeviceBillOrderList() {
    return deviceBillOrderList;
  }

  public void addDeviceBillOrderList(DeviceBillOrder deviceBillOrder) {
    this.deviceBillOrderList.add(deviceBillOrder);
  }

  public List<DeviceBillActivation> getDeviceBillActivationList() {
    return deviceBillActivationList;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public DeviceUsage getDeviceUsage() {
    return deviceUsage;
  }

  public void setDeviceUsage(DeviceUsage deviceUsage) {
    this.deviceUsage = deviceUsage;
  }

  public List<DeviceBillActive> getDeviceBillActiveList() {
    return deviceBillActiveList;
  }

  public List<DeviceBillData> getDeviceBillDataList() {
    return deviceBillDataList;
  }

  public void addDeviceBillDataList(DeviceBillData deviceBillData) {
    if(deviceBillData!=null)
      this.deviceBillDataList.add(deviceBillData);
  }

  public List<DeviceBillSms> getDeviceBillSmsList() {
    return deviceBillSmsList;
  }

  public void addDeviceBillSmsList(DeviceBillSms deviceBillSms) {
    if(deviceBillSms!=null)
      this.deviceBillSmsList.add(deviceBillSms);
  }

  public List<DeviceBillVoice> getDeviceBillVoiceList() {
    return deviceBillVoiceList;
  }

  public void addDeviceBillVoiceList(DeviceBillVoice deviceBillVoice) {
    if(deviceBillVoice!=null)
      this.deviceBillVoiceList.add(deviceBillVoice);
  }

  public List<DeviceBillPrepay> getDeviceBillPrepayList() {
    return deviceBillPrepayList;
  }

  public void addDeviceBillPrepayList(DeviceBillPrepay deviceBillPrepay) {
    if(deviceBillPrepay!=null)
      this.deviceBillPrepayList.add(deviceBillPrepay);
  }

  public DeviceBill getDeviceBill() {
    return deviceBill;
  }

  public void setDeviceBill(DeviceBill deviceBill) {
    this.deviceBill = deviceBill;
  }
}
