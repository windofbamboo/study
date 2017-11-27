package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.mdb.common.daos.BaseDao;
import com.ai.iot.mdb.common.daos.KeyId;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import com.ai.iot.mdb.common.mdb.entity.SegInfo;
import com.ai.iot.mdb.common.mdb.entity.TotalInfo;
import com.ai.iot.mdb.common.mdb.loader.MoLoadable;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.util.List;

public class DeviceMo extends MoBase implements MoLoadable {
    private final static Logger logger = LoggerFactory.getLogger(DeviceMo.class);

    private Long deviceId;
    private String iccid;
    private Long acctId;
    private Long custId;
    private String operatorCode;
    private String provinceCode;
    private String msisdn;
    private String imsi;
    private String imei;
    private int simBarred;
    private int overageLimitOverwrite;
    private int status;
    private Long updateTime;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getSimBarred() {
        return simBarred;
    }

    public void setSimBarred(int simBarred) {
        this.simBarred = simBarred;
    }

    public int getOverageLimitOverwrite() {
        return overageLimitOverwrite;
    }

    public void setOverageLimitOverwrite(int overageLimitOverwrite) {
        this.overageLimitOverwrite = overageLimitOverwrite;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
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
        return MoBaseRegisterFactory.hashField_DEVICE;
    }

    @Override
    public int getFieldType() {
        return MdbTables.FIELD_TYPE_OBJECT;
    }

    //获取本MO的总体信息：物理表中的ID总数，最小ID，最大ID。
    @Override
    public TotalInfo getTotalInfo(Connection conn, QueryRunner qr) {
        TotalInfo totalInfo = null;
        try {
            totalInfo = BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getDeviceMoTotalInfo");
        } catch (Exception e) {
            logger.error("getPlatSmsValue err ", e);
        }
        return totalInfo;
    }

    //把所有的ID划分成多个片，自己定义ID分片的方法，对于DeviceMo,默认采用AccountMo的分片方法：
    //例如一下5条ID，将会被分成5片：3000000000000000~300099999999999、3100000000000000~310099999999999、
    //3200000000000000~320099999999999、3300000000000000~330099999999999、3400000000000000~340099999999999
    //示例ID：
    //3000000001000111
    //3000000002000111
    //3000000002000112
    //3000000003000112
    //3400000003000111
    @Override
    public List<SegInfo> sliceIdsIntoSegs(TotalInfo totalInfo) {
        if (CheckNull.isNull(totalInfo)) {
            return null;
        }

        return AccountMo.defaultSliceIds(totalInfo,100, 1000000000000L);
    }

    //分片去捞所有ID。
    //资料加载的时候根据ID，一片一片的取，为了达到边取边分发加载的效果。
    @Override
    public List<KeyId> fetchBatchIds(Connection conn, QueryRunner qr, SegInfo segInfo) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getDeviceIdSeg", segInfo.getMinId(), segInfo.getMaxId());
    }

    //派发的ID是不是本MO的key中的ID。
    //如加载DeviceInfo时，是按deviceId 派发的，那么就return true;
    //而一条设备记录相关的DeviceImsi之类的Mo, 因为派发时的DeviceId 并不是DeviceImsiMo的Key中的id，此时就需要返回false.
    @Override
    public boolean isKeyId() {
        return true;
    }

    //返回本MO对应的派发类型
    @Override
    public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
        return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_DEVICE;
    }

    //本MO从物理库加载的具体实现。单条
    @Override
    public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getMdbDeviceById", id);
    }

    //本MO从物理库加载的具体实现。多条
    @Override
    public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getMdbDeviceById", id);
    }
}
