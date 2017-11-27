package com.ai.iot.bill.common.mq;

import com.ai.iot.bill.common.param.BaseParamDao;
import com.ai.iot.bill.common.param.ConnHostBean;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.ZkLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**zk操作类
 * Created by geyunfeng on 2017/7/10.
 */
public class ZkMgr {

  private static final Logger logger = LoggerFactory.getLogger(ZkMgr.class);

  public static ZkLock getPartitionLock(final int zkClustterNo, String znode, int partition) {

    List<ConnHostBean> connHostBeanList = BaseParamDao.getConnHostByRouteType(zkClustterNo);
    if (connHostBeanList == null || connHostBeanList.isEmpty()) {
      logger.info("未能获取到zookeeper集群的连接信息:{}",zkClustterNo);
      return null;
    }

    StringBuilder connStr = new StringBuilder();
    for (ConnHostBean connHostBean : connHostBeanList) {
      if (connStr.length() == 0) {
        connStr.append(connHostBean.getHostIp());
        connStr.append(":");
        connStr.append(connHostBean.getHostPort());
      } else {
        connStr.append(",");
        connStr.append(connHostBean.getHostIp());
        connStr.append(":");
        connStr.append(connHostBean.getHostPort());
      }
    }

//    String passWd = null;
//    ConnectBean connectBean = BaseParamDao.getConnByRouteType(BaseDefine.CONNTYPE_ZK_M2F);
//    if (connectBean != null) {
//      passWd = SysUtil.decode(connectBean.getPassWord());
//    }

    ZkLock zk = new ZkLock(connStr.toString());
    int isOk = zk.getLock(znode, partition);
    if (isOk != Const.OK) {
      zk.close();
      zk = null;
    }
    return zk;
  }


}
