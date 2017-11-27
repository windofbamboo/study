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

public class DeviceImsiMo extends MoBase implements MoLoadable {
    //private final static Logger logger = LoggerFactory.getLogger(DeviceImsiMo.class);

    private String imsi;
    private Long deviceId;
    private Long startDate;
    private Long endDate;

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
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
        key.append(MoBaseRegisterFactory.mdbOperaCode_IMSI);
        key.append(MoBaseRegisterFactory.separator);
        key.append(imsi);
        return key.toString().getBytes();
    }

    @Override
    public void setKeyId(String keyId) {
        imsi = keyId;
    }

    @Override
    public String getField() {
        return MoBaseRegisterFactory.hashField_DEVICE_IMSI;
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
        return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getDeviceImsiByDeviceId", id);
    }

    @Override
    public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getDeviceImsiByDeviceId", id);
    }
}
