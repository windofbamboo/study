package com.ai.iot.bill.common.route;

import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.param.BaseParamDao;
import com.ai.iot.bill.common.param.ConnectBean;
import com.ai.iot.bill.common.param.RouteBean;
import com.ai.iot.bill.common.util.SysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**路由类
 * Created by geyunfeng on 2017/7/10.
 */
public class RouteMgr {

    private static final Logger logger = LoggerFactory.getLogger(RouteMgr.class);

    private static Map<Integer ,List<RouteBean>> routeMap= new HashMap<>();//key为 td_b_route.ROUTE_TYPE
    private static Map<Integer,String> connectMap= new HashMap<>();//key为 CONN_ID,value 为 CONN_CODE
    private static boolean initTag=false;
    private static Lock tLock = new ReentrantLock();


    public static List<ConnectBean> getConnectBeanList(final int connectType){
        if(!init()){
            logger.error("init err !");
            return Collections.emptyList();
        }
        return BaseParamDao.getConnectByRouteType(connectType);
    }

    public static String getDbConnect(final int connectType){
        return getDbConnect(connectType, BaseDefine.PROVINCE_CODE_COMMON,(long)0);
    }

    public static String getDbConnect(final int connectType,final long id){
        return getDbConnect(connectType,BaseDefine.PROVINCE_CODE_COMMON,id);
    }

    public static String getDbConnect(final int connectType,final String provinceCode){
        return getDbConnect(connectType,provinceCode,(long)0);
    }

    public static String getDbConnect(final int connectType,final String provinceCode,final long id){

        if(connectType!=BaseDefine.CONNTYPE_MYSQL_CRM &&
           connectType!=BaseDefine.CONNTYPE_MYSQL_BILL &&
           connectType!=BaseDefine.CONNTYPE_MYSQL_ACCT){
            logger.error("this method only for pdb !");
            return null;
        }

        if(!init()){
            logger.error("init err !");
            return null;
        }

        List<RouteBean> routeBeanList= routeMap.get(connectType);
        if(routeBeanList==null||routeBeanList.isEmpty()){
            logger.error("connectType :"+connectType+" is empty !");
            return null;
        }

        RouteBean tRouteBean=null;
        boolean isFound=false;
        for(RouteBean routeBean:routeBeanList){
            long compId=id;
            if(routeBean.getComputerMode()==BaseDefine.COMPUTE_MODE_MOD){
                compId=Math.floorMod(id,10000);
            }
            if(Integer.parseInt(provinceCode) == routeBean.getProvinceCode() &&
               routeBean.getMin()<=compId &&  routeBean.getMax()>=compId){
                tRouteBean=routeBean;
                isFound=true;
                break;
            }
        }

        if(!isFound){
            for(RouteBean routeBean:routeBeanList){
                long compId=id;
                if(routeBean.getComputerMode()==BaseDefine.COMPUTE_MODE_MOD){
                    compId=Math.floorMod(id,10000);
                }
                if(BaseDefine.PROVINCE_CODE_COMMON.equals(provinceCode) &&
                        routeBean.getMin()<=compId && routeBean.getMax()>=compId){
                    tRouteBean=routeBean;
                    isFound=true;
                    break;
                }
            }
        }

        if(!isFound){
            logger.error("can not match route data !");
            return null;
        }

        if(connectMap.containsKey(tRouteBean.getSourceId())){
            return connectMap.get(tRouteBean.getSourceId());
        }
        logger.error("sourceId: "+tRouteBean.getSourceId()+" can not found in TD_B_CONNECT !");
        return null;
    }

    public static boolean init(){
        try {
            tLock.lock();
            if(initTag){
                return true;
            }
            if(initParam()){
                initTag=true;
                return true;
            }
            return false;
        }catch (Exception e){
            logger.error("initParam err ! ");
            return false;
        }finally {
            tLock.unlock();
        }
    }

    private static boolean initParam(){

        try {
            List<ConnectBean> connectList= BaseParamDao.getConnectList();
            List<RouteBean> routeList = BaseParamDao.getRouteList();
            if(connectList == null || connectList.isEmpty()){
                return false;
            }
            if(routeList == null || routeList.isEmpty()){
                return false;
            }
            try {
                for(ConnectBean connectBean :connectList){
                    connectMap.put(connectBean.getConnId(), connectBean.getConnCode());
                }
                routeMap = SysUtil.getIdMap(routeList);
            }catch (Exception e){
                logger.error("data deal err! ");
                return false;
            }
            return true;
        }catch (Exception e){
            logger.error("initParam err! ");
            return false;
        }
    }




}
