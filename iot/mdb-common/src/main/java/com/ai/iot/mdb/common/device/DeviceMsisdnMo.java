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

public class DeviceMsisdnMo extends MoBase implements MoLoadable {
    //private final static Logger logger = LoggerFactory.getLogger(DeviceMsisdnMo.class);

    private String msisdn;
    private Long deviceId;
    private Long startDate;
    private Long endDate;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    @Override
    public byte[] getKey() {
        StringBuffer key = new StringBuffer();
        key.append(MoBaseRegisterFactory.mdbOperaCode_MSISDN);
        key.append(MoBaseRegisterFactory.separator);
        key.append(msisdn);
        return key.toString().getBytes();
    }

    @Override
    public void setKeyId(String keyId) {
        msisdn = keyId;
    }

    @Override
    public String getField() {
        return MoBaseRegisterFactory.hashField_DEVICE_MSISDN;
    }

    @Override
    public int getFieldType() {
        return MdbTables.FIELD_TYPE_LIST;
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

    @Override
    public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
        return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_DEVICE;
    }

    @Override
    public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getDeviceMsisdnByDeviceId", id);
    }

    @Override
    public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getDeviceMsisdnByDeviceId", id);
    }
}
