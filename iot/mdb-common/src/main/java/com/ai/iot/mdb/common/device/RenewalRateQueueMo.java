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

public class RenewalRateQueueMo extends MoBase implements MoLoadable {
    //private final static Logger logger = LoggerFactory.getLogger(RenewalRateQueue.class);
    private long deviceId;
    private long ratePlanId;
    private long startDate;
    private long endDate;
    private long updateTime;
    
    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getRatePlanId() {
        return ratePlanId;
    }

    public void setRatePlanId(long ratePlanId) {
        this.ratePlanId = ratePlanId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
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
        return MoBaseRegisterFactory.hashField_RENEWAL_RATE_QUEUE;
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

    //返回本MO对应的派发类型
    @Override
    public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
        return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_DEVICE;
    }

    //本MO从物理库加载的具体实现。单条
    @Override
    public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getMdbRenewalRateQueueById", id);
    }

    //本MO从物理库加载的具体实现。多条
    @Override
    public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getMdbRenewalRateQueueById", id);
    }
}
