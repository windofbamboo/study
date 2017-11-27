package com.ai.iot.mdb.common.mdb.loader;

import com.ai.iot.mdb.common.daos.KeyId;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import com.ai.iot.mdb.common.mdb.entity.SegInfo;
import com.ai.iot.mdb.common.mdb.entity.TotalInfo;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.util.List;

public interface MoLoadable {
    //PDB加载相关
    public abstract byte[] getKey();
    public abstract void setKeyId(String keyId);
    public abstract TotalInfo getTotalInfo(Connection conn, QueryRunner qr);
    public abstract List<SegInfo> sliceIdsIntoSegs(TotalInfo totalInfo);
    public abstract List<KeyId> fetchBatchIds(Connection conn, QueryRunner qr, SegInfo segInfo);
    public abstract boolean isKeyId();
    public abstract MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType();
    public abstract MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr);
    public abstract List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr);
}
