package com.ai.iot.bill.common.paramcachemanager.core.loader;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheConfigure;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheManagerConfigure;
import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbClient4Param;
import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.CheckNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数加载器 实现以下功能
 * 1、把参数从物理库加载到内存库。
 * 2、对所有的PO组进行加载。
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/10 09:36]
 * @Version:      [v1.0]
 */
public class ParamLoader {
    private final Logger logger = LoggerFactory.getLogger(ParamLoader.class);
    private Map<String, ParamGroupLoader> poGroups;
    private CustJedisCluster jc;
    public final static String mdbOperaCode_POG_LIST = "300";
    public final static String keySuffix_POG_LIST = "POG";
    public final static String mdbOperaCode_POG_INFO = "301";
    public final static String hashField_POG_INFO = "POGROUP_INFO";
    public final static String mdbOperaCode_PO_SIZE = "302";
    public final static String keySuffix_POG_SIZE = "SIZE";
    public final static String hashField_PO_SIZE = "POGROUP_PO_DATA_SIZE";
    public final static String mdbOperaCode_N_POBASE = "303";
    public final static String hashField_N_POBASE = "POGROUP_PO_DATA";
    public final static String mdbOperaCode_POG_PO_INDEX = "304";
    public final static String keySuffix_POG_PO_INDEX = "INDEX";
    public final static String hashField_POG_PO_INDEX  = "POGROUP_PO_INDEX";
    public final static String separator = "+";

    ///po组基本信息
    public final static class POG_INFO_VERSION implements Serializable {
        private static final long serialVersionUID = 2017072110359945002L;
        private Long dbUpdateFlag;//14位时间
        private Long mdbUpdateFlag;//14位时间
        private Long mdbLastUpdateFlag;//MDB上次更新的时间，需要删除

        public Long getDbUpdateFlag() {
            return dbUpdateFlag;
        }

        public void setDbUpdateFlag(Long dbUpdateFlag) {
            this.dbUpdateFlag = dbUpdateFlag;
        }

        public Long getMdbUpdateFlag() {
            return mdbUpdateFlag;
        }

        public void setMdbUpdateFlag(Long mdbUpdateFlag) {
            this.mdbUpdateFlag = mdbUpdateFlag;
        }

        public Long getMdbLastUpdateFlag() {
            return mdbLastUpdateFlag;
        }

        public void setMdbLastUpdateFlag(Long mdbLastUpdateFlag) {
            this.mdbLastUpdateFlag = mdbLastUpdateFlag;
        }
    }

    public ParamLoader(ParamLoaderConfigure paramLoaderConfigure) throws Exception {
        jc = RedisMgr.getJedisCluster(paramLoaderConfigure.getParamLoadMdbRouteType());
        try {
            ParamCacheManagerConfigure.initialize(ParamCacheManagerConfigure.ConfigureEnum.PROPERTIES_FILE);
            ParamCacheManagerConfigure.getGlobalInstance().init();
        } catch (Exception e) {
            logger.error("ParamCacheManagerConfigure init failed.");
        }

        ParamCacheConfigure configure =ParamCacheManagerConfigure.getGlobalInstance();
        configure.setSerializeType(1);
        try {
            MdbClient4Param.getGlobalInstance().initialize(configure.getMdbConnectType(),
                    configure.getMdbParamHostAndPorts(),
                    configure.getMdbParamPassword(),
                    configure.isMdbParamUseMaster());
        } catch (POException e) {
            logger.error("MdbClient4Param.getGlobalInstance().initialize failed. exception:{}", e.getMessage());
        }

        PoGroupRegisterFactory.create();
        poGroups = new HashMap<>();
        for (String groupName : PoGroupRegisterFactory.getAllPoGroupObjects().keySet()) {
            PoGroupRegister poGroupRegister = PoGroupRegisterFactory.getPoObjects(groupName);
            ParamGroupLoader pogLoader = new ParamGroupLoader(poGroupRegister, paramLoaderConfigure);
            poGroups.put(groupName, pogLoader);
            if (CheckNull.isNull(jc)) {
                logger.info("############jc is null.############### groupName = {}, key is:{}", groupName, generatePogKey());
            }
            logger.info("############groupName = {}, key is:{} ###############", groupName, generatePogKey());
            //加载所有的组名
            jc.sadd(generatePogKey().getBytes(), groupName.getBytes());
        }
        System.out.println("ParamLoader end...");
    }

    public void run() throws InterruptedException, ParamLoadException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        //检查是否有更新
        Map<String, POG_INFO_VERSION> poGroupsToUpdate = isUpdate();

        //如果没有更新就休息设定的间隔时间
        if (poGroupsToUpdate.isEmpty()) {
            logger.info("############ParamLoader run()->poGroupsToUpdate.isEmpty() wait ....###########");
            return;
        }

        //循环更新各PO组
        for (Map.Entry<String, POG_INFO_VERSION> entry : poGroupsToUpdate.entrySet()) {
            if (entry.getKey().equals("POG_ZONE")) {
                logger.info("##POG_ZONE##");
            }
            ParamGroupLoader paramGroupLoader = poGroups.get(entry.getKey());
            Long version = entry.getValue().dbUpdateFlag;
            if (!paramGroupLoader.getDataFromPDB()) {
                return;
            }

            if (paramGroupLoader.saveDataToMDB(version)) {
                if (!paramGroupLoader.commitUpdateFlagToMDB(entry.getValue())) {
                    logger.error(new ParamLoadException(ParamLoadException.ExceptionENUM.COMMIT_UPDATEFLAG_TOMDB_FAILED).getMessage());
                    return;
                }

                if (!paramGroupLoader.cleanOldDataInMDB(entry.getValue())) {
                    logger.error(new ParamLoadException(ParamLoadException.ExceptionENUM.CLEAN_OLD_DATA_FAILED).getMessage());
                    return;
                }
            }

        }

        System.out.println("run end....");
    }

    //返回值 Map<String, Date>  String  需要更新的Po组名  Date需要更新的Po组的最新时间戳
    public Map<String, POG_INFO_VERSION> isUpdate() throws ParamLoadException {
        //遍历各个PO组，看是否有需要更新，得到需要更新的PO组相关信息
        Map<String, POG_INFO_VERSION> updateFlags = new HashMap<>();
        for (Map.Entry<String, ParamGroupLoader> entry : poGroups.entrySet()) {
            String pogName = entry.getKey();
            POG_INFO_VERSION updateFlag = isPogUpToDate(pogName);
            if (null==updateFlag) {
                continue;
            }
            updateFlags.put(pogName, updateFlag);
        }

        return updateFlags;
    }

    //查看该PO组是否需要更新，需要更新时，返回最新的时间戳，不需要更新时，返回null
    //pogName ：PO组名
    //返回值：内存库中最新的版本号，如果有值，则实际上已经落后于物理库中的版本号
    public POG_INFO_VERSION isPogUpToDate(String pogName) throws ParamLoadException {
        //获取物理库的PO组更新标记
        Long updateFlagDb = ParamLoaderDao.getUpdateFlag(pogName);

        //获取内存库的PO组更新标记 并进行比较
        //MdbParamDataWrapper.PARAM_GROUP poGroupInfo = MdbParamDataWrapper.getPoGroupInfo(pogName);
        String pogUpdateFlagKey = generatePogInfoKey(pogName);
        MdbParamDataWrapper.ParamGroup poGroupInfo = ParamGroupLoader.getObjectData(jc.get(pogUpdateFlagKey.getBytes()), 1);
        if (null == poGroupInfo) {
            poGroupInfo = new MdbParamDataWrapper.ParamGroup();
            poGroupInfo.setCurrentVersion(2017072018050000L);
            //byte[] bytes = ParamGroupLoader.serializeObj(poGroupInfo, MdbParamDataWrapper.getSerializeType());
            byte[] bytes = ParamGroupLoader.serializeObj(poGroupInfo, 1);
            jc.set(pogUpdateFlagKey.getBytes(), bytes);
            logger.info("ParamLoader[{}]::isPogUpToDate() executed.", pogName);
        }
        Long lastVersion = poGroupInfo.getLastVersion();
        long updateFlagMdb = poGroupInfo.getCurrentVersion();

        POG_INFO_VERSION pogInfoVersion = new POG_INFO_VERSION();
        pogInfoVersion.setDbUpdateFlag(updateFlagDb);
        pogInfoVersion.setMdbUpdateFlag(updateFlagMdb);
        pogInfoVersion.setMdbLastUpdateFlag(lastVersion);
        if (updateFlagMdb != updateFlagDb) {
            return pogInfoVersion;
        }

        //默认不更新
        return null;
    }

    public String generatePogKey() {
        return mdbOperaCode_POG_LIST + separator + ParamLoader.keySuffix_POG_LIST;
    }

    public String generatePogInfoKey(String pogName) {
        return mdbOperaCode_POG_INFO + separator + pogName;
    }
}
