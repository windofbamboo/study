package com.ai.iot.bill.common.param;

import com.ai.iot.bill.common.db.DataSourceMgr;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class BaseParamDao {

    private static final Logger logger = LoggerFactory.getLogger(BaseParamDao.class);

    public static ConnectBean getConnByRouteType (final int routeType){

        List<ConnectBean> connectBeanList =getConnectByRouteType(routeType);
        if(connectBeanList==null || connectBeanList.isEmpty()){
            return null;
        }
        return connectBeanList.get(0);
    }


    public static List<ConnectBean> getConnectByRouteType (final int routeType){
        String sqlstr=" select b.CONN_ID as connId ,b.CONN_CODE as connCode,b.CONNECT_STR as connectStr ,b.PDB_USER as pdbUser,b.PASSWD as passWord " +
                " from TD_B_ROUTE a,TD_B_CONNECT b " +
                " where a.SOURCE_ID=b.CONN_ID and a.ROUTE_TYPE= ?";
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(ConnectBean.class),routeType );
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr,String.valueOf(routeType)); 
            return Collections.emptyList();
        }
    }

    public static List<ConnHostBean> getConnHostByRouteType (final int routeType){
        String sqlstr=" select b.CONN_ID as connId,b.HOST_TYPE as hostType,b.HOST_IP as hostIp,b.HOST_PORT as hostPort " +
                " from TD_B_ROUTE a,TD_B_CONN_HOST b " +
                " where a.SOURCE_ID=b.CONN_ID and a.ROUTE_TYPE=?";
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(ConnHostBean.class),routeType);
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr,String.valueOf(routeType)); 
            return Collections.emptyList();
        }
    }

    public static List<ConnPropertyBean> getConnPropertyByRouteType (final int routeType){
        String sqlstr=" select b.CONN_ID as connId ,b.PROP_TYPE as propType ,b.PROP_NAME as propName,b.PROP_VALUE as propValue " +
                " from TD_B_ROUTE a,TD_B_CONN_PROPERTY b " +
                " where a.SOURCE_ID=b.CONN_ID and a.ROUTE_TYPE=?";

        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(ConnPropertyBean.class),routeType);
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr,String.valueOf(routeType)); 
            return Collections.emptyList();
        }
    }

    public static List<ConnPropertyBean> getConnPropertyByConnId (final int connId){
        String sqlstr=" select b.CONN_ID as connId ,b.PROP_TYPE as propType ,b.PROP_NAME as propName,b.PROP_VALUE as propValue" +
                " from TD_B_CONN_PROPERTY b " +
                " where b.CONN_ID =?";

        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(ConnPropertyBean.class),connId);
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr,String.valueOf(connId)); 
            return Collections.emptyList();
        }
    }



    public static ConnectBean getConnectByConnCode (final String connCode){
        String sqlstr="select CONN_ID as connId ,CONN_CODE as connCode,CONNECT_STR as connectStr,PDB_USER as pdbUser,PASSWD as passWord " +
                " from TD_B_CONNECT" +
                " where CONN_CODE= ?";
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            List<ConnectBean> connectBeanList =qr.query(sqlstr, new BeanListHandler<>(ConnectBean.class),connCode);
            if(connectBeanList==null||connectBeanList.isEmpty()){
                return null;
            }
            return connectBeanList.get(0);
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr,connCode); 
            return null;
        }
    }

    public static ConnectBean getConnectByConnId (final int connId){
        String sqlstr="select CONN_ID as connId,CONN_CODE as connCode,CONNECT_STR as connectStr,PDB_USER as pdbUser,PASSWD as passWord " +
            " from TD_B_CONNECT" +
            " where CONN_ID= ?";
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
            QueryRunner qr = new QueryRunner(ds);
            List<ConnectBean> connectBeanList =qr.query(sqlstr, new BeanListHandler<>(ConnectBean.class),connId);
            if(connectBeanList==null||connectBeanList.isEmpty()){
                return null;
            }
            return connectBeanList.get(0);
        }catch (Exception e){
            sqlErrorLog(e,sqlstr);
            return null;
        }
    }

    public static List<ConnectBean> getConnectList (){
        String sqlstr="select CONN_ID as connId,CONN_CODE as connCode,CONNECT_STR as connectStr,PDB_USER as pdbUser,PASSWD as passWord " +
                " from TD_B_CONNECT" ;
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(ConnectBean.class));
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr); 
            return Collections.emptyList();
        }
    }

    public static List<RouteBean> getRouteList (){
        String sqlstr=" select ROUTE_TYPE as routeType,PROVINCE_CODE as provinceCode,COMPUTER_MODE as computerMode," +
                      " MIN as min,MAX as max,SOURCE_ID as sourceId " +
                      " from TD_B_ROUTE" ;
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(RouteBean.class));
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr); 
            return Collections.emptyList();
        }
    }

    public static ParamBean getParamByTypeAndName(String type, String name) {
        String sqlstr=" select param_value as paramValue,param_value2 as paramValue2,param_value3 as paramValue3 from TD_B_PARAM " +
                      " where param_type=? and param_name=?" ;

        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanHandler<>(ParamBean.class),type,name);
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr,type,name);         
            return null;
        }
    }

    public static List<ParamBean> getParamByType(String... types) {
    	String sqlstr=null;
    	
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            StringBuilder addStr = new StringBuilder();
            if(types.length==1){
                addStr.append("='").append(types[0]).append("'");
            }else{
                for (String type : types) {
                    if (addStr.length() == 0) {
                        addStr.append(" in ('").append(type).append("'");
                    } else {
                        addStr.append(",'").append(type).append("'");
                    }
                }
                addStr.append(")");
            }

            sqlstr = "select param_name as paramName,param_value as paramValue,param_value2 as paramValue2 " +
                          ",param_value3 as paramValue3 from TD_B_PARAM where param_type " + addStr.toString() +
                          " order by param_name,param_value" ;
            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(ParamBean.class));
        }catch (Exception e){
        	sqlErrorLog(e,sqlstr);
            return Collections.emptyList();
        }
    }
    
    /*
     * 获取confName的属性，放到conf中
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getConf(Map conf,String confName){
    	List<ParamBean> params = BaseParamDao.getParamByType(confName);
		for(ParamBean param:params){
			conf.put(param.getParamName(), param.getParamValue());
		}
    }

    public static void sqlErrorLog(Exception e,String sqlstr,String... params){
    	logger.error("sql execute err:{}",e);   
    	logger.error("sql :{}\n\t params: {} ",sqlstr,params);
    }
}
