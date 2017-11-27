package com.ai.iot.bill.common.db;

import com.ai.iot.bill.common.util.SqlUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**sqlxml执行方法
 * Created by geyunfeng on 2017/7/22.
 */
public class XmlBaseDao extends JdbcBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(XmlBaseDao.class);

    public static <T> T selectOne (String dbName, String sqlId, Object... params){
        return selectOne(dbName,null,sqlId,params);
    }

    public static <T> T selectOne (String dbName, Map<String,String> props, String sqlId, Object... params){
        List<T> tList=selectList(dbName,props,sqlId,params);
        if(tList==null||tList.isEmpty())
            return null;
        return tList.get(0);
    }

    public static <T> List<T> selectList (String dbName, String sqlId, Object... params){
        return selectList(dbName,null,sqlId,params);
    }
    public static <T> List<T> selectList (String dbName, Map<String,String> props, String sqlId, Object... params){

        XmlSql xmlSql=XmlMgr.getXmlSql(sqlId);
        if(xmlSql==null){
            logger.error("xmlSql is null!");
            return Collections.emptyList();
        }
        if(props!=null&&!props.isEmpty()){
            SqlUtil.replaceSqlProperties(props,xmlSql.getParseSql());
        }

        Class<?> type= null;
        try {
            type = Class.forName(xmlSql.getResultType());
        } catch (ClassNotFoundException e) {
            logger.error("get class err : {}" , e);
        }

		    @SuppressWarnings("unchecked")
			List<T> tList=(List<T>) selectList(dbName,xmlSql.getParseSql().toString(),type,params);
        return tList;
    }

    public static boolean execsql (String dbName,String sqlId,Object... params){
        return execsql(dbName,null,sqlId,params);
    }

    public static boolean execsql (String dbName,Map<String,String> props,String sqlId,Object... params){

        XmlSql xmlSql=XmlMgr.getXmlSql(sqlId);
        if(xmlSql==null){
            logger.error("xmlSql is null!");
            return false;
        }

        if(props!=null&&!props.isEmpty()){
            SqlUtil.replaceSqlProperties(props,xmlSql.getParseSql());
        }

        try {
            DataSource ds =DataSourceMgr.getDataSource(dbName);
            QueryRunner qr = new QueryRunner(ds);
            qr.update(xmlSql.getParseSql().toString(),params);
        }catch (Exception e){
            logger.error("sql execute err: {}" , e);
            return false;
        }
        
        return true;
    }

    public static <T> void execOne (String dbName, String sqlId, T tRecord){
        execOne(dbName,null,sqlId,tRecord);
    }

    public static <T> void execOne (String dbName, Map<String,String> props, String sqlId, T tRecord){
        List<T> tList = new ArrayList<>();
        tList.add(tRecord);
        execBatch(dbName,props,sqlId,tList);
    }

    public static <T> boolean execBatch (String dbName, String sqlId, List<T> tList){
        return execBatch(dbName,null,sqlId,tList);
    }

    public static <T> boolean execBatch (String dbName, Map<String,String> props, String sqlId, List<T> tList){

        if(tList==null||tList.isEmpty()){
        	return false;
        }
        XmlSql xmlSql=XmlMgr.getXmlSql(sqlId);
        if(xmlSql==null){
            logger.error("xmlSql is null!");
            return false;
        }

        if(props!=null&&!props.isEmpty()){
           SqlUtil.replaceSqlProperties(props,xmlSql.getParseSql());
        }

        Object[][] params;
        try {
            params = SqlUtil.getParams(xmlSql.getVariables(),tList);
        } catch (Exception e) {
            logger.error("getParams data err : {}" , e);
            return false;
        }

        try {
            DataSource ds =DataSourceMgr.getDataSource(dbName);
            QueryRunner qr = new QueryRunner(ds);
            qr.batch(xmlSql.getParseSql().toString(), params != null ? params : new Object[0][]);
        }catch (Exception e){
            logger.error("sql execute err: {}" , e);
            return false;
        }
        return true;
    }


}
