package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.mdb.common.daos.BaseDao;
import com.ai.iot.mdb.common.daos.KeyId;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import com.ai.iot.mdb.common.mdb.entity.SegInfo;
import com.ai.iot.mdb.common.mdb.entity.TotalInfo;
import com.ai.iot.mdb.common.mdb.loader.MoLoadable;
import org.apache.commons.dbutils.QueryRunner;
import java.sql.Connection;
import java.util.List;

public class DeviceOthersMo extends MoBase implements MoLoadable {
    //private final static Logger logger = LoggerFactory.getLogger(DeviceOthersMo.class);

    private long deviceId;
    private int status;
    private long receivingConfirmDate;//RECEIVING_CONFIRM_DATE
    private long shippedDate;//SHIPPED_DATE
    private long updateTime;

    private String customerCustom1;
    private String customerCustom2;
    private String customerCustom3;
    private String customerCustom4;
    private String customerCustom5;

    private String accountCustom1;
    private String accountCustom2;
    private String accountCustom3;
    private String accountCustom4;
    private String accountCustom5;
    private String accountCustom6;
    private String accountCustom7;
    private String accountCustom8;
    private String accountCustom9;
    private String accountCustom10;

    private String provinceCustom1;
    private String provinceCustom2;
    private String provinceCustom3;
    private String provinceCustom4;
    private String provinceCustom5;

    private String providerCustom1;
    private String providerCustom2;
    private String providerCustom3;
    private String providerCustom4;
    private String providerCustom5;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getReceivingConfirmDate() {
        return receivingConfirmDate;
    }

    public void setReceivingConfirmDate(long receivingConfirmDate) {
        this.receivingConfirmDate = receivingConfirmDate;
    }

    public long getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(long shippedDate) {
        this.shippedDate = shippedDate;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCustomerCustom1() {
        return customerCustom1;
    }

    public void setCustomerCustom1(String customerCustom1) {
        this.customerCustom1 = customerCustom1;
    }

    public String getCustomerCustom2() {
        return customerCustom2;
    }

    public void setCustomerCustom2(String customerCustom2) {
        this.customerCustom2 = customerCustom2;
    }

    public String getCustomerCustom3() {
        return customerCustom3;
    }

    public void setCustomerCustom3(String customerCustom3) {
        this.customerCustom3 = customerCustom3;
    }

    public String getCustomerCustom4() {
        return customerCustom4;
    }

    public void setCustomerCustom4(String customerCustom4) {
        this.customerCustom4 = customerCustom4;
    }

    public String getCustomerCustom5() {
        return customerCustom5;
    }

    public void setCustomerCustom5(String customerCustom5) {
        this.customerCustom5 = customerCustom5;
    }

    public String getAccountCustom1() {
        return accountCustom1;
    }

    public void setAccountCustom1(String accountCustom1) {
        this.accountCustom1 = accountCustom1;
    }

    public String getAccountCustom2() {
        return accountCustom2;
    }

    public void setAccountCustom2(String accountCustom2) {
        this.accountCustom2 = accountCustom2;
    }

    public String getAccountCustom3() {
        return accountCustom3;
    }

    public void setAccountCustom3(String accountCustom3) {
        this.accountCustom3 = accountCustom3;
    }

    public String getAccountCustom4() {
        return accountCustom4;
    }

    public void setAccountCustom4(String accountCustom4) {
        this.accountCustom4 = accountCustom4;
    }

    public String getAccountCustom5() {
        return accountCustom5;
    }

    public void setAccountCustom5(String accountCustom5) {
        this.accountCustom5 = accountCustom5;
    }

    public String getAccountCustom6() {
        return accountCustom6;
    }

    public void setAccountCustom6(String accountCustom6) {
        this.accountCustom6 = accountCustom6;
    }

    public String getAccountCustom7() {
        return accountCustom7;
    }

    public void setAccountCustom7(String accountCustom7) {
        this.accountCustom7 = accountCustom7;
    }

    public String getAccountCustom8() {
        return accountCustom8;
    }

    public void setAccountCustom8(String accountCustom8) {
        this.accountCustom8 = accountCustom8;
    }

    public String getAccountCustom9() {
        return accountCustom9;
    }

    public void setAccountCustom9(String accountCustom9) {
        this.accountCustom9 = accountCustom9;
    }

    public String getAccountCustom10() {
        return accountCustom10;
    }

    public void setAccountCustom10(String accountCustom10) {
        this.accountCustom10 = accountCustom10;
    }

    public String getProvinceCustom1() {
        return provinceCustom1;
    }

    public void setProvinceCustom1(String provinceCustom1) {
        this.provinceCustom1 = provinceCustom1;
    }

    public String getProvinceCustom2() {
        return provinceCustom2;
    }

    public void setProvinceCustom2(String provinceCustom2) {
        this.provinceCustom2 = provinceCustom2;
    }

    public String getProvinceCustom3() {
        return provinceCustom3;
    }

    public void setProvinceCustom3(String provinceCustom3) {
        this.provinceCustom3 = provinceCustom3;
    }

    public String getProvinceCustom4() {
        return provinceCustom4;
    }

    public void setProvinceCustom4(String provinceCustom4) {
        this.provinceCustom4 = provinceCustom4;
    }

    public String getProvinceCustom5() {
        return provinceCustom5;
    }

    public void setProvinceCustom5(String provinceCustom5) {
        this.provinceCustom5 = provinceCustom5;
    }

    public String getProviderCustom1() {
        return providerCustom1;
    }

    public void setProviderCustom1(String providerCustom1) {
        this.providerCustom1 = providerCustom1;
    }

    public String getProviderCustom2() {
        return providerCustom2;
    }

    public void setProviderCustom2(String providerCustom2) {
        this.providerCustom2 = providerCustom2;
    }

    public String getProviderCustom3() {
        return providerCustom3;
    }

    public void setProviderCustom3(String providerCustom3) {
        this.providerCustom3 = providerCustom3;
    }

    public String getProviderCustom4() {
        return providerCustom4;
    }

    public void setProviderCustom4(String providerCustom4) {
        this.providerCustom4 = providerCustom4;
    }

    public String getProviderCustom5() {
        return providerCustom5;
    }

    public void setProviderCustom5(String providerCustom5) {
        this.providerCustom5 = providerCustom5;
    }

    @Override
    public byte[] getKey() {
        StringBuffer key = new StringBuffer();
        key.append(MoBaseRegisterFactory.mdbOperaCode_DEVICE_ID);
        key.append(MoBaseRegisterFactory.separator);
        key.append(deviceId);
        return key.toString().getBytes();
    }

    @Override
    public void setKeyId(String keyId) {
        deviceId = Long.parseLong(keyId);
    }

    @Override
    public String getField() {
        return MoBaseRegisterFactory.hashField_DEVICE_OTHERS;
    }

    @Override
    public int getFieldType() {
        return MdbTables.FIELD_TYPE_OBJECT;
    }

    @Override
    public TotalInfo getTotalInfo(Connection conn, QueryRunner qr) {
      return null;
    }

    @Override
    public List<SegInfo> sliceIdsIntoSegs(TotalInfo totalInfo) {
      return null;
    }

    @Override
    public List<KeyId> fetchBatchIds(Connection conn, QueryRunner qr, SegInfo segInfo) {
      return null;
    }

    @Override
    public boolean isKeyId() {
      return false;
    }

    //返回本MO对应的派发类型
    @Override
    public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
        return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_DEVICE;
    }

    //本MO从物理库加载的具体实现。单条
    @Override
    public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getMdbDeviceOthersById", id);
    }

    //本MO从物理库加载的具体实现。多条
    @Override
    public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getMdbDeviceOthersById", id);
    }
}
