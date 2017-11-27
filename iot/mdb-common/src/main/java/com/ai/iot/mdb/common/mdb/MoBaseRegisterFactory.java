package com.ai.iot.mdb.common.mdb;

import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.redisLdr.TABLE_ID;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.daos.BaseDao;
import com.ai.iot.mdb.common.daos.KeyId;
import com.ai.iot.mdb.common.daos.MoMo;
import com.ai.iot.mdb.common.mdb.entity.TotalInfo;
import com.ai.iot.mdb.common.mdb.loader.MoLoadable;
import com.ai.iot.mdb.common.mdb.mog.AutoRuleMoGroup;
import com.ai.iot.mdb.common.mdb.mog.DeviceInfoMoGroup;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoBaseRegisterFactory {
    private final static Logger logger = LoggerFactory.getLogger(MoBaseRegisterFactory.class);
    public final static String separator = Const.KEY_JOIN;
    public final static String mdbOperaCode_DEVICE_ID = RedisConst.DevInfoMdbKey.MDB_KEY_DEVICE_ID_START_DATE;
    public final static String mdbOperaCode_MSISDN = RedisConst.DevInfoMdbKey.MDB_KEY_DEVICE_MSISDN;
    public final static String mdbOperaCode_IMSI = RedisConst.DevInfoMdbKey.MDB_KEY_DEVICE_IMSI;
    public final static String mdbOperaCode_ACCOUNT_ID = RedisConst.DevInfoMdbKey.MDB_KEY_ACCOUNT_BASE_INFO;
    public final static String hashField_DEVICE =TABLE_ID.DEVINFO.TF_F_DEVICE_HASHKEY;
    public final static String hashField_RATE_PLAN = TABLE_ID.DEVINFO.TF_F_DEVICE_RATE_PLAN_HASHKEY;
    public final static String hashField_DEVICE_OTHERS =TABLE_ID.DEVINFO.TF_F_DEVICE_OTHER_HASHKEY;
    public final static String hashField_RENEWAL_RATE_QUEUE =TABLE_ID.DEVINFO.TF_F_RENEWAL_RATE_QUEUE_HASHKEY;
    public final static String hashField_DEVICE_MSISDN = TABLE_ID.DEVINFO.TF_F_DEVICE_MSISDN_ID_HASHKEY;
    public final static String hashField_DEVICE_IMSI = TABLE_ID.DEVINFO.TF_F_DEVICE_IMSI_ID_HASHKEY;
    public final static String hashField_ACCOUNT = TABLE_ID.DEVINFO.TF_F_ACCT_HASHKEY;
    public final static String hashField_ACCOUNT_APPLICATION = TABLE_ID.DEVINFO.TF_F_ACCT_APPLICATION_HASHKEY;

    private static Map<String, MoBase> moBaseMap = new HashMap<>();
    private static Map<String, List<String>> relatedMos = new HashMap<>();
    private static Map<String, TotalInfo> idTotals = new HashMap<>();
    private static Map<String, List<KeyId>> keyIds = new HashMap<>();
    private static Map<String, MoGroup> moGroupMap = new HashMap<>();
    private static boolean moGroupInited = false;
    ///定义MOC的名字
    public static enum MoGroupEnum{
        MOG_DEVINFO("MOG_DEVINFO"),
        MOG_AUTORULE("MOG_AUTORULE");
        private MoGroupEnum(String s){
            name=s;
        }
        public String getName() {
            return name;
        }
        private String name;
    }

    public static enum MdbKeyDispTypeEnum {
        DISP_TYPE_DEVICE("DISP_TYPE_DEVICE"),
        DISP_TYPE_ACCOUNT("DISP_TYPE_ACCOUNT"),
        DISP_TYPE_EVENT_INSTID("DISP_TYPE_EVENT_INSTID"),

        /* autorule start */
        DISP_TYPE_AUTORULE("DISP_TYPE_AUTORULE"),
        DISP_TYPE_AUTORULE_OPER("DISP_TYPE_AUTORULE_OPER"),
        DISP_TYPE_SUB_ACCT("DISP_TYPE_SUB_ACCT")
        /* autorule end */
        ;
        private MdbKeyDispTypeEnum(String s){
            name=s;
        }
        public String getName() {
            return name;
        }
        private String name;
    }

    public static void initAllMoGroup() {
        MoGroup moGroup = new DeviceInfoMoGroup();
        moGroupMap.put(moGroup.getName(), moGroup);

        //增加PO组需要在此新增注册
        moGroup = new AutoRuleMoGroup();
        moGroupMap.put(moGroup.getName(), moGroup);

        return;
    }

    public static boolean isMoGroupInited() {
        return moGroupInited;
    }

    public static void setMoGroupInited(boolean moGroupInited) {
        MoBaseRegisterFactory.moGroupInited = moGroupInited;
    }

    public static boolean initRelatedMos() {
        String sql = "SELECT PARAM_VALUE AS moName, PARAM_VALUE2 AS relatedMoName  \n" +
                "FROM TD_B_PARAM\n" +
                "WHERE PARAM_TYPE='MO_RELATION_DEVINFO'";
        List<MoMo> moMos = BaseDao.selectList(BaseDefine.CONNCODE_MYSQL_PARAM, sql, MoMo.class);
        if (CheckNull.isNull(moMos)) {
            return true;
        }

        for (MoMo moMo : moMos) {
            if (!relatedMos.containsKey(moMo.getMoName())) {
                relatedMos.put(moMo.getMoName(), null);
            }
        }

        for (String moName : relatedMos.keySet()) {
            List<String> relatedMoNames = new ArrayList<>();
            for (MoMo moMo : moMos) {
                if (moMo.getMoName().equals(moName)) {
                    relatedMoNames.add(moMo.getRelatedMoName());
                    logger.info("##########initRelatedMos()-> moName:{}, relatedMoName:{}->##############", moName, moMo.getRelatedMoName());
                }
            }
            relatedMos.put(moName, relatedMoNames);
        }

        return true;
    }

    public static Map<String, MoBase> initAllMos(Connection conn) {
        QueryRunner qr = new QueryRunner();
        for (Map.Entry<String, MoGroup> entry : moGroupMap.entrySet()) {
            MoGroup moGroup = entry.getValue();
            Map<String, MoBase> moBases = moGroup.getMoBases();
            if (CheckNull.isNull(moBases)) {
                logger.warn("MoBaseRegisterFactory::initAllMos() {}'s moBases is empty.", moGroup.getName());
                continue;
            }

            for (Map.Entry<String, MoBase> moBaseEntry : moBases.entrySet()) {
                MoBase moBase = moBaseEntry.getValue();
                moBaseMap.put(moBase.getName(), moBase);

                //传进来的conn如果不是Null就加载Id总量信息。
                if(!CheckNull.isNull(conn)) {
                    if (!((MoLoadable)moBase).isKeyId() || idTotals.containsKey(((MoLoadable)moBase).getKeyDispType().toString())) {
                        continue;
                    }
                    TotalInfo totalInfo = ((MoLoadable)moBase).getTotalInfo(conn, qr);
                    idTotals.put(moBase.getName(), totalInfo);
                }
            }
        }

        initRelatedMos();
        return moBaseMap;
    }

    public static MoBase getMoBaseByName(String moName) {
        return moBaseMap.get(moName);
    }

    public static List<MoBase> getRelatedMos(String moName) {
        logger.info("#########MoBaseRegisterFactory::getRelatedMos() --begin moName:{}########", moName);
        if (CheckNull.isNull(relatedMos)) {
            logger.info("#########MoBaseRegisterFactory::getRelatedMos() relatedMos is null. moName:{}########", moName);
            return null;
        }

        List<String> moNames = relatedMos.get(moName);
        if (CheckNull.isNull(moNames)) {
            logger.info("#########MoBaseRegisterFactory::getRelatedMos() relatedMos.get(moName) is null. moName:{}########", moName);
            return null;
        }

        List<MoBase> moBases = new ArrayList<>();
        for (String relateMoName : moNames) {
            MoBase moBase = moBaseMap.get(relateMoName);
            if (!CheckNull.isNull(moBase)) {
                moBases.add(moBase);
                logger.info("#########MoBaseRegisterFactory::getRelatedMos() relateMoName:{}########", relateMoName);
            }
        }

        logger.info("#########MoBaseRegisterFactory::getRelatedMos() --end moName:{}########", moName);
        return moBases;
    }

    public static List<KeyId> getDispIdsByType(String moBaseType) {
        return keyIds.get(moBaseType);
    }

    public static Map<String, TotalInfo> getIdTotals() {
        return idTotals;
    }

    public static Map<String, MoGroup> getMoGroupMap() {
        return moGroupMap;
    }
}
