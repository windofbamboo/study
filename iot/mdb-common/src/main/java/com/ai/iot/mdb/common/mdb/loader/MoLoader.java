package com.ai.iot.mdb.common.mdb.loader;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.MoBase;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;

public interface MoLoader {
    public int loadRelatedMos(String id, MoBase moBase);
    public boolean saveDataToMdb(CustJedisCluster jc, MoBase moBase);
    public boolean removeMoFromMdb(CustJedisCluster jc, MoBase moBase);
    public Connection getConn();
    public void setConn(Connection conn);
    public QueryRunner getQr();
    public void setQr(QueryRunner qr);
}
