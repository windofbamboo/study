package com.ai.iot.bill.common.db;

import com.ai.iot.bill.common.util.SqlUtil;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**z数据库操作
 * Created by geyunfeng on 2017/7/21.
 */
public class JdbcBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(JdbcBaseDao.class);

    public static List<List<String>> getStringList(String connCode,String sqlStr, String... params) {

        Connection conn = null;
        PreparedStatement pstmt =null;
        ResultSet rs =null;
        try {
            DataSource ds= DataSourceMgr.getDataSource(connCode);
            if(ds!=null){
                conn = ds.getConnection();
                pstmt = conn.prepareStatement(sqlStr);
                if(params!=null){
                    for(int i=0;i<params.length;i++){
                        pstmt.setString(i+1,String.valueOf(params[i]));
                    }
                }
                rs = pstmt.executeQuery();
                return getColumnList(rs);
            }
            return Collections.emptyList();
        }catch (Exception e){
            logger.error("exec sql err :{}, sql:{}" , e, sqlStr);
            return Collections.emptyList();
        }finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("rs close err :{}" , e);
                }
            }
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    logger.error("pstmt close err :{}" , e);
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("conn close err :{}" , e);
                }
            }
        }
    }


    private static List<List<String>> getColumnList(ResultSet rs){

        List<List<String>> columnList=new ArrayList<>();
        try {
            int col = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                List<String> tList=new ArrayList<>();
                for (int i = 1; i <= col; i++) {
                    tList.add(rs.getString(i));
                }
                columnList.add(tList);
            }
        } catch (SQLException e) {
            logger.error("SQLException : {}" , e);
            return Collections.emptyList();
        }
        return columnList;
    }


    public static int  getInt (DataSource ds, String sqlStr,Object... params){
        try {
            QueryRunner qr = new QueryRunner(ds);
            return qr.query(sqlStr, new ScalarHandler<>(),params);
        }catch (Exception e){
            logger.error("sql execute err : {}" , e);
            return 0;
        }
    }

    public static int  getCount (DataSource ds, String sqlStr,Object... params){
        try {
            QueryRunner qr = new QueryRunner(ds);
            Object ob = qr.query(sqlStr, new ScalarHandler<>(),params);
            return Integer.parseInt(ob.toString());

        }catch (Exception e){
            logger.error("sql execute err: {}" , e);
            return 0;
        }
    }
    
    public static int  getCount (String dbName, String sqlStr,Object... params){
        DataSource ds= DataSourceMgr.getDataSource(dbName);
        return getCount (ds, sqlStr,params);
    }

    /**
     * select 操作，返回 单条记录
     */
    public static <T> T  selectOne (DataSource ds, String sqlStr, Class<T> type, Object... params){
        try {
            QueryRunner qr = new QueryRunner(ds);

            CustomBeanProcessor convert = new CustomBeanProcessor();
            RowProcessor rp = new BasicRowProcessor(convert);
            ResultSetHandler<T> bh = new BeanHandler<>(type, rp);

            return qr.query(sqlStr, bh,params);
        }catch (Exception e){
            logger.error("sql execute err: {}" , e);
            return null;
        }
    }
    
    public static <T> T  selectOne (String dbName, String sqlStr, Class<T> type, Object... params){
    	DataSource ds= DataSourceMgr.getDataSource(dbName);
    	return selectOne (ds, sqlStr,type, params);
    }

    /**
     * select 操作，返回List<Object> 类型
     */
    public static <T> List<T>  selectList (DataSource ds, String sqlStr, Class<T> type, Object... params){
        try {
            QueryRunner qr = new QueryRunner(ds);

            CustomBeanProcessor convert = new CustomBeanProcessor();
            RowProcessor rp = new BasicRowProcessor(convert);
            ResultSetHandler<List<T>> bh = new BeanListHandler<>(type, rp);

            return qr.query(sqlStr, bh,params);
        }catch (Exception e){
            logger.error("sql execute err: {}" , e);
            return Collections.emptyList();
        }
    }
    
    public static <T> List<T>  selectList (String dbName, String sqlStr, Class<T> type, Object... params){
    	DataSource ds= DataSourceMgr.getDataSource(dbName);
    	return selectList ( ds,  sqlStr,  type, params);
    }

    /**
     * 根据传入的参数，执行sql语句；不对sql本身做任何解析的操作
     */
    public static boolean execsql (DataSource ds,String sqlStr,Object... params){
        try {
            QueryRunner qr = new QueryRunner(ds);
            qr.update(sqlStr,params);
            return true;
        }catch (Exception e){
            logger.error("sql execute err: {}" , e);
            return false;
        }
    }
    
    public static boolean execsql (String dbName,String sqlStr,Object... params){
    	DataSource ds =DataSourceMgr.getDataSource(dbName);
    	return execsql ( ds, sqlStr,params);
    }

    /**
     * 批量执行update,delete,insert语句,根据List<Object>中的数据,进行绑定变量操作
     * sql语句中的变量，名为规则为：#{connId}，其中connId为Object中的属性，#{}为固定格式
     */
    public static <T> boolean execBatch (String dbName, String sqlStr, List<T> tList){

        if(tList==null||tList.isEmpty()){
            return true;
        }

        List<String> variables=new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        if(!SqlUtil.parseSqlStr(sqlStr,variables,sql)){
            logger.error("parse sql err!");
            return false;
        }

        Object[][] params;
        try {
            params = SqlUtil.getParams(variables,tList);
        } catch (Exception e) {
            logger.error("getParams data err: {}" , e);
            return false;
        }

        DataSource ds;
        try {
            ds= DataSourceMgr.getDataSource(dbName);
        } catch (Exception e) {
            logger.error("getDataSource err: {}" , e);
            return false;
        }
        if(ds==null){
            logger.error("DataSource is null !");
            return false;
        }

//        Connection conn;
//        try {
//            conn=ds.getConnection();
//        } catch (SQLException e) {
//            logger.error("get Connection err: {}" , e);
//            return false;
//        }
//        if(conn==null){
//            logger.error("Connection is null !");
//            return false;
//        }

//        try {
//            if(conn.getAutoCommit()){
//               conn.setAutoCommit(false);
//            }
//        } catch (SQLException e) {
//            logger.error("setAutoCommit err: {}" , e);
//        }

        try {
            QueryRunner qr = new QueryRunner(ds);
            qr.batch(sql.toString(), params != null ? params : new Object[0][]);
        }catch (Exception e){
            logger.error("sql execute err: {}" , e);
            return false;
        }
//        finally {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                logger.error("conn close err: {}" ,e);
//            }
//        }
        
        return true;
    }



}
