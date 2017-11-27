package com.ai.iot.bill.common.paramcachemanager.core.loader;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.PropertiesUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 参数加载器的DAO层实现
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/12 09:36]
 * @Version:      [v1.0]
 */
public class ParamLoaderDao {

    private static final Logger logger = LoggerFactory.getLogger(ParamLoaderDao.class);

    private static Properties dbProperties = PropertiesUtil.getProperties("po_sql");

    /**
     * PO组的更新标志
     * @Author:       [zhangrui]
     * @CreateDate:   [2017/7/12 09:36]
     * @Version:      [v1.0]
     */
    public static class PogUpdateFlag {
        private String pogName;
        private Date updateFlag;

        public String getPogName() {
            return pogName;
        }

        public void setPogName(String pogName) {
            this.pogName = pogName;
        }

        public Date getUpdateFlag() {
            return updateFlag;
        }

        public void setUpdateFlag(Date updateFlag) {
            this.updateFlag = updateFlag;
        }

        public Long toLong() {
            String dbUpdateFlagStr = new SimpleDateFormat("yyyyMMddHHmmss").format(updateFlag);
            return Long.parseLong(dbUpdateFlagStr);
        }
    }

    /**
     *  获取PO组的版本号
     *  @param pogName  PO组名
     *  @return  PO组的版本号
     *  @since  1.2
     */
    public static Long getUpdateFlag(String pogName) {
        String sqlstr=" SELECT UPDATE_FLAG as updateFlag FROM td_update_group_flag " +
                " WHERE TAB_GROUP_NAME= ? ";
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
            QueryRunner qr = new QueryRunner(ds);
            PogUpdateFlag pogUpdateFlag = qr.query(sqlstr, new BeanHandler<PogUpdateFlag>(PogUpdateFlag.class), pogName);
            if (CheckNull.isNull(pogUpdateFlag)) {
                return 0L;
            }

            pogUpdateFlag.setPogName(pogName);
            return pogUpdateFlag.toLong();
        }catch (Exception e){
            logger.error("sql execute err! sql:{}, pogName:{}", sqlstr, pogName);
            return 0L;
        }
    }

    /**
     *  根据PO名获取PO的所有数据
     *  @param poName  PO名
     *  @return  PO的所有数据（单个PO是以List<String>来表示的，每个String代表PO的一个字段）
     *  @since  1.2
     */
    public static List<List<String>> getPoBases(String poName) {
        String sqlstr = dbProperties.getProperty(poName);
        String connCode = BaseDefine.CONNCODE_MYSQL_PARAM;
        if ("PoAutoRuleOperCont".equals(poName) || "PoAutorule".equals(poName)) {
            connCode = BaseDefine.CONNCODE_MYCAT_CRM;
        }

        if (null==sqlstr || sqlstr.isEmpty()) {
            logger.error("your this po's sql is not configured. poName: {}", poName);
            return null;
        }



        try {
            List<List<String>> pobases = JdbcBaseDao.getStringList(connCode, sqlstr);
            return pobases;
        } catch (Exception e){
            logger.error("sql execute err! ");
            return null;
        }
    }
}
