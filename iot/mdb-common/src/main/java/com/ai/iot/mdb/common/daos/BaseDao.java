package com.ai.iot.mdb.common.daos;

import com.ai.iot.bill.common.db.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseDao extends JdbcBaseDao {

  private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

  public static Connection getConnection(String connCode) {

    DataSource ds = null;
    try {
      ds = DataSourceMgr.getDataSource(connCode);
    } catch (Exception e) {
      logger.error("get DataSource err! ", e);
      return null;
    }

    Connection conn = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
    } catch (Exception e) {
      logger.error("get Connection err! ", e);
    }
    return conn;
  }


  public static <T extends Object> T selectOne(QueryRunner qr, Connection conn, String sqlId, Object... params) {
    return selectOne(qr, conn, null, sqlId, params);
  }

  public static <T extends Object> T selectOne(QueryRunner qr,
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

  public static <T extends Object> List<T> selectList(QueryRunner qr, Connection conn, String sqlId, Object... params) {
    return selectList(qr, conn, null, sqlId, params);
  }

  public static <T extends Object> List<T> selectList(QueryRunner qr,
                                                      Connection conn,
                                                      Map<String, String> props,
                                                      String sqlId,
                                                      Object... params) {

    XmlSql xmlSql = XmlMgr.getXmlSql(sqlId);

    if (props != null && !props.isEmpty()) {
      SqlUtil.replaceSqlProperties(props, xmlSql.getParseSql());
    }

    @SuppressWarnings("rawtypes")
    Class type = null;
    try {
      type = Class.forName(xmlSql.getResultType());
    } catch (ClassNotFoundException e) {
      logger.error("Class :"+xmlSql.getResultType()+" NotFound!", e);
      return null;
    }

    try {
      CustomBeanProcessor convert = new CustomBeanProcessor();
      RowProcessor rp = new BasicRowProcessor(convert);
      @SuppressWarnings("unchecked")
      ResultSetHandler<List<T>> bh = new BeanListHandler<T>(type, rp);

      return qr.query(conn, xmlSql.getParseSql().toString(), bh, params);
    } catch (Exception e) {
      logger.error("xmlSql：{}", xmlSql.getParseSql().toString());
      logger.error("sql execute err!", e);
      return null;
    }
  }

  public static <T extends Object> void execsql(QueryRunner qr, Connection conn, String sqlId, Object... params) {
    execsql(qr, conn, null, sqlId, params);
  }

  public static <T extends Object> void execsql(QueryRunner qr,
                                                Connection conn,
                                                Map<String, String> props,
                                                String sqlId,
                                                Object... params) {

    XmlSql xmlSql = XmlMgr.getXmlSql(sqlId);

    if (props != null && !props.isEmpty()) {
      SqlUtil.replaceSqlProperties(props, xmlSql.getParseSql());
    }

    try {
      qr.update(conn, xmlSql.getParseSql().toString(), params);
    } catch (Exception e) {
      logger.error("sql execute err!", e);
    }
  }

  public static <T extends Object> void execOne(QueryRunner qr, Connection conn, String sqlId, T tRecord) {
    execOne(qr, conn, null, sqlId, tRecord);
  }

  public static <T extends Object> void execOne(QueryRunner qr,
                                                Connection conn,
                                                Map<String, String> props,
                                                String sqlId,
                                                T tRecord) {
    List<T> tList = new ArrayList<T>();
    tList.add(tRecord);
    execBatch(qr, conn, props, sqlId, tList);
  }

  public static <T extends Object> void execBatch(QueryRunner qr, Connection conn, String sqlId, List<T> tList) {
    execBatch(qr, conn, null, sqlId, tList);
  }

  public static <T extends Object> void execBatch(QueryRunner qr,
                                                  Connection conn,
                                                  Map<String, String> props,
                                                  String sqlId,
                                                  List<T> tList) {

    if (tList == null || tList.isEmpty()) {
      return;
    }
    XmlSql xmlSql = XmlMgr.getXmlSql(sqlId);

    if (props != null && !props.isEmpty()) {
      SqlUtil.replaceSqlProperties(props, xmlSql.getParseSql());
    }

    Object[][] params = null;
    try {
      params = SqlUtil.getParams(xmlSql.getVariables(), tList);
    } catch (Exception e) {
      logger.error("getParams data err!", e);
      return;
    }

    try {
      qr.batch(conn, xmlSql.getParseSql().toString(), params);
    } catch (Exception e) {
      logger.error("sql execute err!", e);
    }
  }
}
