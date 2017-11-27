package com.ai.iot.bill.common.mgr;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.param.ConnectBean;
import com.ai.iot.bill.common.route.RouteMgr;
import com.ai.iot.bill.common.util.SysUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by geyunfeng on 2017/7/10.
 */
public class RouteTest {

    public static void main(String[] args) {

        String pw="billing@10T";
        System.out.println(SysUtil.encode(pw));

        DataSource ds = DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_CRM);

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
                return;
            }
        }

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String connCode= RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, 5000);
        System.out.println(connCode);

        List<ConnectBean> connectBeanList=RouteMgr.getConnectBeanList(BaseDefine.CONNTYPE_MYSQL_CRM);
        System.out.println(connectBeanList);

    }
}
