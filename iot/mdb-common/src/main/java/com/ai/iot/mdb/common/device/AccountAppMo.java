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

public class AccountAppMo extends MoBase implements MoLoadable {
    //private final static Logger logger = LoggerFactory.getLogger(AccountAppMo.class);

    private Long acctId;
    private Long testDataLimit;
    private Long testVoiceLimit;
    private Long testSmsLimit;
    private Long startDate;
    private Long endDate;

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public Long getTestDataLimit() {
        return testDataLimit;
    }

    public void setTestDataLimit(Long testDataLimit) {
        this.testDataLimit = testDataLimit;
    }

    public Long getTestVoiceLimit() {
        return testVoiceLimit;
    }

    public void setTestVoiceLimit(Long testVoiceLimit) {
        this.testVoiceLimit = testVoiceLimit;
    }

    public Long getTestSmsLimit() {
        return testSmsLimit;
    }

    public void setTestSmsLimit(Long testSmsLimit) {
        this.testSmsLimit = testSmsLimit;
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
        key.append(MoBaseRegisterFactory.mdbOperaCode_ACCOUNT_ID);
        key.append(MoBaseRegisterFactory.separator);
        key.append(acctId);
        return key.toString().getBytes();
    }

    @Override
    public void setKeyId(String keyId) {
        acctId = Long.parseLong(keyId);
    }

    @Override
    public String getField() {
        return MoBaseRegisterFactory.hashField_ACCOUNT_APPLICATION;
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
        return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_ACCOUNT;
    }

    @Override
    public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
        //TODO: 内存表要求有start_date, end_date 但是物理表中没有
        return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getAccountAppByAcctId", id);
    }

    @Override
    public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
        //TODO: 内存表要求有start_date, end_date 但是物理表中没有
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getAccountAppByAcctId", id);
    }
}
