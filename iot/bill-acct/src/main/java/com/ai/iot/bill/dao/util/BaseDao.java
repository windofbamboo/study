package com.ai.iot.bill.dao.util;

import com.ai.iot.bill.common.db.CustomBeanProcessor;
import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.db.XmlMgr;
import com.ai.iot.bill.common.db.XmlSql;
import com.ai.iot.bill.common.util.SqlUtil;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**基础sql操作类
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class BaseDao extends JdbcBaseDao {

  private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

  public static Connection getConnection(String connCode) {

    DataSource ds ;
    try {
      ds = DataSourceMgr.getDataSource(connCode);
    } catch (Exception e) {
      logger.error("get DataSource err.{}", e);
      return null;
    }
    if(ds == null){
      logger.error("get DataSource err! ");
      return null;
    }

    int retryTimes = 0;
    Connection conn = null;
    while(conn == null && retryTimes<60){
      try {
        conn = ds.getConnection();
        if(conn == null){
          Thread.sleep(3000);
          retryTimes++;
        }
      } catch (Exception e) {
        logger.info("can't get Connection err.{}", e);
      }
    }

    if(conn!=null){
      try {
        conn.setAutoCommit(false);
      } catch (SQLException e) {
        logger.error("setAutoCommit false err.{}", e);
      }
    }else{
      logger.error("can't get Connection !");
    }

    return conn;
  }
  
  public static <T> T selectOne(QueryRunner qr, Connection conn, String sqlId, Object... params) {
    return selectOne(qr, conn, null, sqlId, params);
  }

  public static <T> T selectOne(QueryRunner qr,
                                               Connection conn,
                                               Map<String, String> props,
                                               String sqlId,
                                               Object... params) {
    List<T> tList = selectList(qr, conn, props, sqlId, params);
    if (tList == null || tList.isEmpty()) {
      return null;
    }
    return tList.get(0);
  }

  public static <T> List<T> selectList(QueryRunner qr, Connection conn, String sqlId, Object... params) {
    return selectList(qr, conn, null, sqlId, params);
  }

  public static <T> List<T> selectList(QueryRunner qr,
                                                      Connection conn,
                                                      Map<String, String> props,
                                                      String sqlId,
                                                      Object... params) {

    XmlSql xmlSql = XmlMgr.getXmlSql(sqlId);

    if(xmlSql == null){
      return Collections.emptyList();
    }

    if (props != null && !props.isEmpty()) {
      SqlUtil.replaceSqlProperties(props, xmlSql.getParseSql());
    }

    Class type;
    try {
      type = Class.forName(xmlSql.getResultType());
    } catch (ClassNotFoundException e) {
      logger.error("Class :"+xmlSql.getResultType()+" NotFound.{}", e);
      return Collections.emptyList();
    }

    try {
      CustomBeanProcessor convert = new CustomBeanProcessor();
      RowProcessor rp = new BasicRowProcessor(convert);
      ResultSetHandler<List<T>> bh = new BeanListHandler<T>(type, rp);

      String sqlStr=xmlSql.getParseSql().toString();
      return qr.query(conn, sqlStr, bh, params);
    } catch (Exception e) {
      logger.error("sql execute err.{}", e);
      return Collections.emptyList();
    }
  }

  public static void execsql(QueryRunner qr, Connection conn, String sqlId, Object... params) {
    execsql(qr, conn, null, sqlId, params);
  }

  public static void execsql(QueryRunner qr,
                                                Connection conn,
                                                Map<String, String> props,
                                                String sqlId,
                                                Object... params) {

    XmlSql xmlSql = XmlMgr.getXmlSql(sqlId);
    if (xmlSql == null){
      return;
    }

    if (props != null && !props.isEmpty()) {
      SqlUtil.replaceSqlProperties(props, xmlSql.getParseSql());
    }

    try {
      qr.update(conn, xmlSql.getParseSql().toString(), params);
    } catch (Exception e) {
      logger.error("sql execute err.{}", e);
    }
  }

  public static <T> void execOne(QueryRunner qr, Connection conn, String sqlId, T tRecord) {
    execOne(qr, conn, null, sqlId, tRecord);
  }

  public static <T> void execOne(QueryRunner qr,
                                                Connection conn,
                                                Map<String, String> props,
                                                String sqlId,
                                                T tRecord) {
    if(tRecord == null ){
      return;
    }
    List<T> tList = new ArrayList<>();
    tList.add(tRecord);
    execBatch(qr, conn, props, sqlId, tList);
  }

  public static <T> void execBatch(QueryRunner qr, Connection conn, String sqlId, List<T> tList) {
    execBatch(qr, conn, null, sqlId, tList);
  }

  public static <T> void execBatch(QueryRunner qr,
                                                  Connection conn,
                                                  Map<String, String> props,
                                                  String sqlId,
                                                  List<T> tList) {

    if (tList == null || tList.isEmpty()) {
      return;
    }
    XmlSql xmlSql = XmlMgr.getXmlSql(sqlId);

    if(xmlSql == null){
      return;
    }

    if (props != null && !props.isEmpty()) {
      SqlUtil.replaceSqlProperties(props, xmlSql.getParseSql());
    }

    Object[][] params ;
    try {
      params = SqlUtil.getParams(xmlSql.getVariables(), tList);
    } catch (Exception e) {
      logger.error("getParams data err.{}", e);
      return;
    }

    try {
      qr.batch(conn, xmlSql.getParseSql().toString(), params);
    } catch (Exception e) {
      logger.error("sql execute err.{}", e);
    }
  }


}
