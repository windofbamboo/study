package com.ai.iot.bill.common.paramcachemanager.core.loader;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoContainer;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoContainerGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoZone;
import com.ai.iot.bill.common.util.CheckNull;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PO组参数加载器 对于某个PO组，实现以下功能
 * 1、从物理库加载参数数据。
 * 2、把参数数据加载到内存库。
 * 3、提交PO组的更新标志到内存库。
 * 4、清理MDB中旧版本的参数数据。
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/10 09:36]
 * @Version:      [v1.0]
 */
public class ParamGroupLoader {
    private static final Logger logger = LoggerFactory.getLogger(ParamGroupLoader.class);
    //加载时物理库的版本号
    protected Long pdbVersion = 0L;
    //加载时内存库的版本号
    protected Long mdbVersion = 0L;

    private CustJedisCluster jc;
    private ParamLoaderConfigure paramLoaderConfigure;
    private static FSTConfiguration serializeTool = FSTConfiguration.createDefaultConfiguration();
    private static FSTConfiguration serializeJsonTool = FSTConfiguration.createJsonConfiguration(false, false);
    protected PoContainerGroup poContainerGroup;

    private PoGroupRegister pogReg;
    public ParamGroupLoader(PoGroupRegister pogReg, ParamLoaderConfigure paramLoaderConfigure) {
        this.pogReg = pogReg;
        poContainerGroup = new PoContainerGroup(pogReg.getPoGroupName(), PoContainerGroup.NONE_INDEX);
        this.paramLoaderConfigure = paramLoaderConfigure;
        logger.info("############ParamGroupLoader::ParamGroupLoader() ->get JC ############### --begin");
        jc = RedisMgr.getJedisCluster(paramLoaderConfigure.getParamLoadMdbRouteType());
        logger.info("############ParamGroupLoader::ParamGroupLoader() ->get JC ############### --end");
        serializeTool.asByteArray("tt");
        serializeJsonTool.asByteArray("tt");//第一次序列化,防止后续序列化格式不同,影响redis的结果
    }

    public boolean getDataFromPDB() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        //遍历PO组中的各个PO
        Map<String, PoBase> pos = pogReg.getAllPoObjects();
        for (HashMap.Entry<String, PoBase> entry : pos.entrySet()) {
            String poName = entry.getKey();
            PoBase po = entry.getValue();
            //创建一个PoContainer 用于盛放捞出来的物理数据
            PoContainer poContainer = null;
            poContainer = new PoContainer(po);

            //捞取物理库数据
            List<List<String>> poBaseList = ParamLoaderDao.getPoBases(poName);
            if (CheckNull.isNull(poBaseList)) {
                continue;
            }

            for (List<String> fieldsList : poBaseList) {
                String poFullName = po.getClass().getName();
                Class<?> subClass = Class.forName(poFullName);
                PoBase poBase = (PoBase)subClass.newInstance();
                try {
                    if (po instanceof PoZone) {
                        logger.info("####temp pause####");
                        if (((PoZone)poBase).getZoneId() == 10000000) {
                            logger.info("####pozone 10000000####");
                        }
                    }
                    subClass.getMethod("fillData", Object.class).invoke(poBase, fieldsList);
                    if (!CheckNull.isNull(poContainer)) {
                        List<PoBase> dataList = poContainer.getPoDataList();
                        dataList.add(poBase);
                    }

                    logger.info("{} is loaded from db.",poName);
                } catch (NoSuchMethodException e) {
                    logger.error("{} NoSuchMethodException error occured...", poBase.getPoName());
                } catch (Exception e) {
                    logger.error("{} fillData error occured...", poBase.getPoName());
                }
            }

            //把查出来的数据塞到本PO对应的PoContainer里去，再把PoContainer放到本PO组的loader的poContainerGroup中去。
            Map<String,PoContainer> poContainerMap = poContainerGroup.getPoContainerMap();
            poContainerMap.put(poName, poContainer);

            logger.info("PogSysLoader::getDataFromPDB() end...");
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectData(byte[] serializableData, int serializeType){
        if(CheckNull.isNull(serializableData))
            return null;
        if (0 == serializeType) {
            return (T)serializeJsonTool.asObject(serializableData);
        }
        return (T)serializeJsonTool.asObject(serializableData);
    }

    public static  <T> byte[] serializeObj(final T obj, int serializeType) {
        if (0 == serializeType) {
            return serializeTool.asByteArray(obj);
        }
        return serializeJsonTool.asByteArray(obj);
    }

    public boolean saveDataToMDB(Long version) {
        logger.info("PogSysLoader::saveDataToMDB()...");
        Map<String,PoContainer> poContainerMap = poContainerGroup.getPoContainerMap();
        if (poContainerMap.isEmpty()) {
            logger.error("PoContainer: {}'s data is empty.", poContainerGroup.getPoGroupName());
            return false;
        }

        for (Map.Entry<String,PoContainer> entry : poContainerMap.entrySet()) {
            PoContainer poContainer = entry.getValue();
            //得到PO各个记录
            int lineNum = 0;
            List<PoBase> poBases = poContainer.getPoDataList();
            for (PoBase poBase : poBases) {
                //写行号和记录对象
                lineNum++;
                //TODO：将来需要把序列化类型改成从资源文件库读取
                if (poBase instanceof PoZone) {
                    logger.info("####temp pause####");
                }
                byte[] bytes = serializeObj(poBase, 1);
                String nKey = generateLineDataKey(lineNum, poBase.getPoGroupName(), poBase.getPoName(), version);
                jc.set(nKey.getBytes(), bytes);
            }

            //更新PO记录数
            String sizeKey = generatePoDataListSizeKey(poContainer.getPoBase().getPoGroupName(), poContainer.getPoBase().getPoName(), version);
            jc.set(sizeKey.getBytes(), String.valueOf(lineNum).getBytes());


        }

        //写入索引名集
        Map<String,Map<String,Map<String,Method>>> indexMethods = PoGroupRegisterFactory.getAllPoGroupIndexMethods();
        for (Map.Entry<String,Map<String,Map<String,Method>>> entry : indexMethods.entrySet()) {
            String pogName = entry.getKey();
            Map<String,Map<String,Method>> poIndexNames = entry.getValue();
            for (Map.Entry<String,Map<String,Method>> poIndexName : poIndexNames.entrySet()) {
                String poName = poIndexName.getKey();
                Map<String,Method> indexNameMethods = poIndexName.getValue();
                for (Map.Entry<String,Method> indexNameMethod : indexNameMethods.entrySet()) {
                    String indexName = indexNameMethod.getKey();
                    String pogPoIndexKey = generatePogPoIndexesNameKey(pogName, poName, version);
                    jc.sadd(pogPoIndexKey.getBytes(), indexName.getBytes());
                }
            }
        }
        return true;
    }


    public boolean commitUpdateFlagToMDB(ParamLoader.POG_INFO_VERSION pogInfoVersion) {
        MdbParamDataWrapper.ParamGroup poGroupInfo = new MdbParamDataWrapper.ParamGroup();
        poGroupInfo.setCurrentVersion(pogInfoVersion.getDbUpdateFlag());
        poGroupInfo.setLastVersion(pogInfoVersion.getMdbUpdateFlag());

        //更新POG信息
        String pogInfoKey = generatePogInfoKey(pogReg.getPoGroupName());
        //TODO:POG info 将来也需要从配置文件读取序列化类型
        byte[] bytes = serializeObj(poGroupInfo, 1);
        jc.set(pogInfoKey.getBytes(), bytes);
        logger.info("ParamGroupLoader::commitUpdateFlagToMDB() end ....");
        return true;
    }

    public boolean cleanOldDataInMDB(ParamLoader.POG_INFO_VERSION pogInfoVersion) {
        //遍历PO组中的各PO
        Map<String, PoBase> pos = pogReg.getAllPoObjects();
        for (HashMap.Entry<String, PoBase> entry : pos.entrySet()) {
            PoBase po =  entry.getValue();

            //获取该PO上次的记录数SIZE
            String sizeKey = generatePoDataListSizeKey(po.getPoGroupName(), po.getPoName(), pogInfoVersion.getMdbLastUpdateFlag());
            byte[] sizeBytes = jc.get(sizeKey.getBytes());

            //如果不存在上一次的记录数，那么就并不需要删除，直接返回成功即可。
            if (null == sizeBytes) {
                return true;
            }

            String sizeStr = new String(sizeBytes);
            int lineNum = Integer.parseInt(sizeStr);

            //删除旧的PO记录集
            for (int i=1; i<=lineNum; i++) {
                String delKey = ParamLoader.mdbOperaCode_N_POBASE + ParamLoader.separator
                        + po.getPoGroupName() + ParamLoader.separator + po.getPoName()
                        + ParamLoader.separator + String.valueOf(pogInfoVersion.getMdbLastUpdateFlag())
                        + ParamLoader.separator + String.valueOf(i);

                jc.del(delKey.getBytes());
            }

            //删除上一次的记录数
            jc.del(sizeKey.getBytes());

            //删除上一次的索引名集
            String pogPoIndexKey = generatePogPoIndexesNameKey(po.getPoGroupName(), po.getPoName(), pogInfoVersion.getMdbLastUpdateFlag());
            jc.del(pogPoIndexKey.getBytes());
        }
        logger.info("ParamGroupLoader::cleanOldDataInMDB() end....");
        return true;
    }

    public Long getPdbVersion() {
        return pdbVersion;
    }

    public void setPdbVersion(Long pdbVersion) {
        this.pdbVersion = pdbVersion;
    }

    public Long getMdbVersion() {
        return mdbVersion;
    }

    public void setMdbVersion(Long mdbVersion) {
        this.mdbVersion = mdbVersion;
    }

    public ParamLoaderConfigure getParamLoaderConfigure() {
        return paramLoaderConfigure;
    }

    public void setParamLoaderConfigure(ParamLoaderConfigure paramLoaderConfigure) {
        this.paramLoaderConfigure = paramLoaderConfigure;
    }

    public static String generatePogInfoKey(String pogName) {
        return  ParamLoader.mdbOperaCode_POG_INFO + ParamLoader.separator
                + pogName;
    }

    public static String generateLineDataKey(int lineNum, String pogName, String poName, Long version) {
        return  ParamLoader.mdbOperaCode_N_POBASE + ParamLoader.separator
                + pogName + ParamLoader.separator + poName
                + ParamLoader.separator + String.valueOf(version)
                + ParamLoader.separator + String.valueOf(lineNum);
    }

    public static String generatePoDataListSizeKey(String pogName, String poName, Long version) {
        return  ParamLoader.mdbOperaCode_PO_SIZE + ParamLoader.separator
                + pogName + ParamLoader.separator + poName
                + ParamLoader.separator + String.valueOf(version)
                + ParamLoader.separator + String.valueOf(ParamLoader.keySuffix_POG_SIZE);
    }

    public static String generatePogPoIndexesNameKey(String pogName, String poName, Long version) {
        return  ParamLoader.mdbOperaCode_POG_PO_INDEX + ParamLoader.separator
                + pogName + ParamLoader.separator + poName
                + ParamLoader.separator + String.valueOf(version)
                + ParamLoader.separator + String.valueOf(ParamLoader.keySuffix_POG_PO_INDEX);
    }
}
