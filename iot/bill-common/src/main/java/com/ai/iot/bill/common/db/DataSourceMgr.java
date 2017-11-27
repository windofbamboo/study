package com.ai.iot.bill.common.db;

import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.param.BaseParamDao;
import com.ai.iot.bill.common.param.ConnPropertyBean;
import com.ai.iot.bill.common.param.ConnectBean;
import com.ai.iot.bill.common.util.PropertiesUtil;
import com.ai.iot.bill.common.util.SysUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**dataSource管理类
 * Created by geyunfeng on 2017/7/18.
 */
public class DataSourceMgr {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceMgr.class);

    private static Map<String,DataSource> dataSourceMap= new HashMap<>();

    private static boolean initTag=false;
    private static Lock sessionLock = new ReentrantLock();

    private DataSourceMgr() {
        throw new IllegalStateException("Utility class");
    }

    public static DataSource getDataSource(String environment){

        if(init(environment)){
            return dataSourceMap.get(environment);
        }
        return null;
    }

    private static boolean init(String environment){

        try {
            sessionLock.lock();
            if(!initTag){
                dataSourceMap.clear();
                if(!initDS(BaseDefine.CONNCODE_MYSQL_PARAM)){
                    return false;
                }
                initTag=true;
            }
            if(!initDS(environment)){
                return false;
            }
        }catch (Exception e){
            logger.error("init err : {}" , e);
            return false;
        }finally {
            sessionLock.unlock();
        }
        return true;
    }


    private static boolean initDS(String environment) {

        if(!dataSourceMap.containsKey(BaseDefine.CONNCODE_MYSQL_PARAM)&&
                !intiDataSource(BaseDefine.CONNCODE_MYSQL_PARAM,null)){
                logger.error("param dataSource init err");
                return false;
        }
        if(!dataSourceMap.containsKey(environment)){
            ConnectBean connectBean=getConnectByConnCode(environment);
            if (connectBean != null) {
                boolean result = intiDataSource(environment,connectBean);
                if(!result){
                    logger.error("{} dataSource init err",environment);
                }
                return result;
            }
        }
        return true;
    }

    private static boolean intiDataSource(String environment,ConnectBean connectBean){
        final String passwordStr = "password";
        DataSource datasource;
        try {
            Properties  dbProperties = PropertiesUtil.getProperties("db");
            if( dbProperties == null){
                logger.error("read file db.properties err !");
                return false;
            }
            PoolProperties p = new PoolProperties();

            if(connectBean!=null){
                if(!BaseDefine.CONNCODE_MYSQL_PARAM.equals(environment)){
                    List<ConnPropertyBean> connPropertyBeanList= BaseParamDao.getConnPropertyByConnId(connectBean.getConnId());
                    if(connPropertyBeanList != null){
                        connPropertyBeanList.stream().filter(connPropertyBean -> connPropertyBean.getPropName() != null && connPropertyBean.getPropValue() != null).forEach(
                            connPropertyBean -> dbProperties.setProperty(connPropertyBean.getPropName(), connPropertyBean.getPropValue()));
                    }
                }
                dbProperties.setProperty("url",connectBean.getConnectStr());
                dbProperties.setProperty("username",connectBean.getPdbUser());
                dbProperties.setProperty(passwordStr,connectBean.getPassWord());
            }

            SysUtil.setObjProperties(p, dbProperties, passwordStr);
            
            logger.info("db-url={}",dbProperties.getProperty("url"));
            logger.info("db-username={}",dbProperties.getProperty("username"));
            logger.info("db-password={}",dbProperties.getProperty(passwordStr));
            
            datasource = new DataSource();
            datasource.setPoolProperties(p);

            dataSourceMap.put(environment, datasource);

        }catch (Exception e){
            logger.error("err: resource err ");
            return false;
        }
        return true;
    }

    public static ConnectBean getConnectByConnCode(final String connCode){

        if(!dataSourceMap.containsKey(BaseDefine.CONNCODE_MYSQL_PARAM)){
            logger.error("param dataSource not init !");
            return null;
        }
        DataSource dataSource = dataSourceMap.get(BaseDefine.CONNCODE_MYSQL_PARAM);
        if(dataSource==null){
            logger.error("param connect err!");
            return null;
        }

        try {
           String sqlstr="select CONN_ID as connId,CONN_CODE as connCode,CONNECT_STR as connectStr,PDB_USER as pdbUser,PASSWD as passWord " +
                         "from TD_B_CONNECT where CONN_CODE= ?";
           QueryRunner qr = new QueryRunner(dataSource);

            return qr.query(sqlstr, new BeanHandler<>(ConnectBean.class),connCode);
        }catch (Exception e){
           logger.error("sql getConnectByConnCode execute err!");
           return null;
        }
    }



}
