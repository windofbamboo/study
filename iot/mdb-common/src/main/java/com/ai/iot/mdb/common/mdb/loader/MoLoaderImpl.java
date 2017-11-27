package com.ai.iot.mdb.common.mdb.loader;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.bill.common.mdb.MoContainer;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.ProtostuffUtil;
import com.ai.iot.mdb.common.autorule.mos.MoAutoRuleWrapper;
import com.ai.iot.mdb.common.autorule.mos.MoAutorule;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoLoaderImpl implements MoLoader {
    private static final Logger logger = LoggerFactory.getLogger(MoLoaderImpl.class);
    private Connection conn;
    private QueryRunner qr;
    public MoLoaderImpl(Connection conn, QueryRunner qr) {
        this.conn = conn;
        this.qr = qr;
    }

    public MoLoaderImpl() {
        this.qr = new QueryRunner();
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public QueryRunner getQr() {
        return qr;
    }

    public void setQr(QueryRunner qr) {
        this.qr = qr;
    }

    public int loadRelatedMos(String id, MoBase moBase) {
        if(CheckNull.isNull(moBase)) 
            return 0;
        //logger.info("#########loadRelatedMos() id:{}, moBase:{}########", id, moBase.toString());
        String name = moBase.getName();
        List<MoBase> relatedMos = MoBaseRegisterFactory.getRelatedMos(name);
        if (CheckNull.isNull(relatedMos)) {
            return 0;
        }

        if (CheckNull.isNull(moBase.getRelatedMoContainers())) {
            moBase.setRelatedMoContainers(new ArrayList<>());
        }

        MoContainer moContainer = null;
        String relatedMoName = "";
        try {
            for (MoBase relatedMo : relatedMos) {
                List<MoBase> moBases = ((MoLoadable)relatedMo).loadDataListFromPdb(conn, id, qr);
                if (!CheckNull.isNull(moBases)) {
                    moContainer = new MoContainer(relatedMo, moBases);
                    moBase.getRelatedMoContainers().add(moContainer);
                }
            }
        } catch (Exception e) {
            logger.error("############loadRelatedMos()->relatedMo.getName():{} Exception.#############", relatedMoName);
        }

        return moBase.getRelatedMoContainers().size();
    }

    public boolean saveDataToMdb(CustJedisCluster jc, MoBase moBase) {
        byte[] key = ((MoLoadable)moBase).getKey();
        byte[] field = CheckNull.isNull(moBase.getField()) ? null:moBase.getField().getBytes();
        if (!CheckNull.isNull(key) && !CheckNull.isNull(field)) {
            byte[] value = null;
            try {
                value = ProtostuffUtil.serializer(moBase);
                if (CheckNull.isNull(value)) {
                    return false;
                }
                jc.hset(key, field, value);
            } catch (Exception e) {
                logger.error("############{} MoLoaderImpl::saveDataToMdb()->saveDataToMdb() ProtostuffUtil.serializer exception.#############", moBase.getName());
                return false;
            }
        } else {
            logger.info("##########{}'s key or field is null. maybe is a wrapper mo.###########", moBase.getName());
        }

        List<MoContainer> containers = moBase.getRelatedMoContainers();
        if (CheckNull.isNull(containers)) {
            logger.info("{} getRelatedMoContainers is null.", moBase.getName());
            return true;
        }

        logger.info("###############query containers moBase is:{}################", moBase.getName());
        try {
            for (MoContainer moContainer : containers) {
                List<MoBase> moBases = moContainer.getMdbBases();
                if (CheckNull.isNull(moBases) || moBases.isEmpty()) {
                    continue;
                }

                Map<String, List<MoBase>> categoryMoBases = categoryMoBasesList(moBases);
                if (CheckNull.isNull(categoryMoBases)) {
                    continue;
                }
                for (Map.Entry<String, List<MoBase>> entry : categoryMoBases.entrySet()) {
                    saveDataListToMdb(jc, entry.getValue());
                }
            }
        } catch (Exception e) {
            logger.warn("##infoload:warn -load mo:{}'s related containers exception. msg:{}##", moBase.getName(), e.getMessage());
        }

        return true;
    }

    //把列表数据塞入value
    private Map<String, List<MoBase>> categoryMoBasesList(List<MoBase> moBases) {
        if (CheckNull.isNull(moBases) || moBases.isEmpty()) {
            return null;
        }

        Map<String, List<MoBase>> categoryMoBases = new HashMap<>();
        for (MoBase mobase : moBases) {
            String key = new String(((MoLoadable)mobase).getKey()) + mobase.getField();
            if (categoryMoBases.containsKey(key)) {
                categoryMoBases.get(key).add(mobase);
            } else {
                List<MoBase> moBaseList = new ArrayList<>();
                moBaseList.add(mobase);
                categoryMoBases.put(key, moBaseList);
            }
        }

        return categoryMoBases;
    }

    public boolean saveDataListToMdb(CustJedisCluster jc, List<MoBase> moBases) {
        byte[] value = ProtostuffUtil.serializerList(moBases);
        if (CheckNull.isNull(value)) {
            return false;
        }

        if (moBases.isEmpty()) {
            return true;
        }
        jc.hset(((MoLoadable)moBases.get(0)).getKey(), moBases.get(0).getField().getBytes(), value);
        return true;
    }

    @Override
    public boolean removeMoFromMdb(CustJedisCluster jc, MoBase moBase) {
        if (CheckNull.isNull(jc) || CheckNull.isNull(moBase)) {
            return false;
        }

        jc.hdel(((MoLoadable)moBase).getKey(), moBase.getField().getBytes());
        return true;
    }
}
