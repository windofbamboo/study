package com.ai.iot.bill.common.mgr;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.db.XmlBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.param.ConnPropertyBean;
import com.ai.iot.bill.common.param.ConnectBean;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by geyunfeng on 2017/7/19.
 */
public class DbTest {

    @SuppressWarnings("unchecked")
	@Test
    public void getDs() {

        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);

            String sqlstr=" select b.CONN_ID as connId,b.PROP_TYPE as propType,b.PROP_NAME as propName,b.PROP_VALUE as propValue " +
                    " from TD_B_CONN_PROPERTY b " +
                    " where b.CONN_ID =?";
            QueryRunner qr = new QueryRunner(ds);

            List<?> rs = qr.query(sqlstr, new MapListHandler(),1011);
            for(Object t : rs) {
                System.out.println((Map<String, Object>)t);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    public void getDs2() {

        try {
            String sqlstr=" select b.CONN_ID as connId,b.PROP_TYPE as propType,b.PROP_NAME as propName " +
                    " from TD_B_CONN_PROPERTY b " +
                    " where b.CONN_ID =?";
            List<List<String>> ColumnList= JdbcBaseDao.getStringList("param",sqlstr,"1011");

            System.out.println(ColumnList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void getDs3() {

        try {
            String sqlstr=" select CONN_ID ,CONN_CODE ,CONNECT_STR ,PDB_USER ,PASSWD from TD_B_CONNECT where CONN_CODE= ?";
            /*List<ConnPropertyBean> connPropertyBeanList= JdbcBaseDao.selectList(BaseDefine.CONNCODE_MYSQL_PARAM,sqlstr,ConnPropertyBean.class,"1011");
            for(ConnPropertyBean connPropertyBean:connPropertyBeanList){
                System.out.println(connPropertyBean.toString());
            }*/

            List<ConnectBean> tList= JdbcBaseDao.selectList(BaseDefine.CONNCODE_MYSQL_PARAM,sqlstr,ConnectBean.class,"crm1");
            System.out.println(tList.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

    }


//    @Test
    public void myTest() {

        List<String> variables=new ArrayList<>();

        String sqlStr=" select b.CONN_ID as connId,b.PROP_TYPE as propType,b.PROP_NAME as propName,b.PROP_VALUE as propValue " +
                " from TD_B_CONN_PROPERTY b " +
                " where b.CONN_ID = #{connId} and b.PROP_TYPE =#{propType} ";
        char[] sqlChar=sqlStr.toCharArray();
        char[] sqlCharNew=new char[sqlChar.length];
        int j=0,st=0;
        boolean isVariable=false;
        for(int i=0;i<sqlChar.length;i++){
            if((sqlChar[i]=='{'||sqlChar[i]=='}')&&!isVariable){
                System.out.println("第"+i+"位分隔符错误 err!");
                return;
            }
            if(sqlChar[i]=='#'){
                if(sqlChar[i+1]!='{'){
                    System.out.println("第"+i+"位分隔符错误 err!");
                    return;
                }
                st=i+2;
                sqlCharNew[j]='?';
                j++;
                isVariable=true;
                continue;
            }
            if(sqlChar[i]=='}'&&isVariable){
                variables.add(sqlStr.substring(st,i));
                isVariable=false;
                continue;
            }

            if(!isVariable){
                sqlCharNew[j]=sqlChar[i];
                j++;
            }
        }
        System.out.println(sqlCharNew);
        System.out.println(variables);
    }

//    @Test
    public void execBatchTest() {

        List<ConnPropertyBean> tList = new ArrayList<ConnPropertyBean>();
        ConnPropertyBean ConnPropertyBean1=new ConnPropertyBean(911,"S","test1","haha");
        ConnPropertyBean ConnPropertyBean2=new ConnPropertyBean(911,"S","test2","haha");

        tList.add(ConnPropertyBean1);
        tList.add(ConnPropertyBean2);

        try {
            String sqlstr=" insert into TD_B_CONN_PROPERTY (CONN_ID,PROP_TYPE,PROP_NAME,PROP_VALUE) " +
                    " values (#{connId},#{propType},#{propName},#{propValue}) ";
            JdbcBaseDao.execBatch(BaseDefine.CONNCODE_MYSQL_PARAM,sqlstr,tList);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Test
    public void xmlBaseDaoTest() {

        XmlBaseDao.selectList(BaseDefine.CONNCODE_MYSQL_PARAM,"baseMapper.getRoute");
        ConnectBean connectBean= XmlBaseDao.selectOne(BaseDefine.CONNCODE_MYSQL_PARAM,"baseMapper.getConnectByConnCode","crm1");
        System.out.println(connectBean);

    }


    @Test
    public void getCount() {

        try {
            String sqlstr=" select count(*) " +
                    " from TD_B_CONN_PROPERTY b " +
                    " where b.CONN_ID =?";
            int num= JdbcBaseDao.getCount("param",sqlstr,"1011");

            System.out.println(num);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
