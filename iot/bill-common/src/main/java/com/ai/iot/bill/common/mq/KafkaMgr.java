package com.ai.iot.bill.common.mq;

import com.ai.iot.bill.common.param.BaseParamDao;
import com.ai.iot.bill.common.param.ConnHostBean;
import com.ai.iot.bill.common.param.ConnPropertyBean;
import com.ai.iot.bill.common.param.ConnectBean;
import com.ai.iot.bill.common.util.SysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**kafka操作类
 * Created by geyunfeng on 2017/7/10.
 */
public class KafkaMgr {

  private static final Logger logger = LoggerFactory.getLogger(KafkaMgr.class);

  public static KafkaMq getKafka(int connType, int aMode) {
    return getKafka(connType, aMode, "iot");
  }

  public static KafkaMq getKafka(int connType, int aMode, String groupId) {
    Map<String, String> propertys = new HashMap<>();
    propertys.put("group.id", groupId);
    propertys.put("max.poll.records", "2000");
    return getKafka(connType, aMode, propertys);
  }

  public static KafkaMq getKafka(int connType, int aMode, Map<String, String> propertys) {

    List<ConnHostBean> connHostBeanList = BaseParamDao.getConnHostByRouteType(connType);
    List<ConnPropertyBean> connPropertyBeanList = BaseParamDao.getConnPropertyByRouteType(connType);

    if(connHostBeanList == null || connHostBeanList.isEmpty()){
      logger.error("KafkaMgr can't find kafka ConnHostBean, connType: " + connType);
      return null;
    }

    if (connPropertyBeanList == null || connPropertyBeanList.isEmpty()) {
      logger.error("KafkaMgr can't find kafka ConnPropertyBean, connType: "+ connType);
      return null;
    }

    String passWd = null;
    ConnectBean connectBean = BaseParamDao.getConnByRouteType(connType);
    if (connectBean != null) {
      if(connectBean.getPassWord()!=null){
        passWd = SysUtil.decode(connectBean.getPassWord());
      }
    }

    KafkaMq mqConn = new KafkaMq(passWd);
    mqConn.connectCluster(connHostBeanList, connPropertyBeanList, propertys, aMode);

    return mqConn;
  }
}
