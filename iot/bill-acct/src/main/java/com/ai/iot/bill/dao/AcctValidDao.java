package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.entity.info.OutAcctTaskBean;
import com.ai.iot.bill.entity.log.DealLog;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 根据出账任务获取账户列表 
 * Created by zhaojiajun on 2017/8/13.
 */
public class AcctValidDao {

	private static final Logger logger = LoggerFactory.getLogger(AcctValidDao.class);
	
	/**
	 * 根据批次ID获取出账批次对象
	 * @return
	 */
	public static DealLog getDealLogById(final long dealId){
		if(dealId == 0){
			return null;
		}
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
            
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select t.TOTAL_NUM as totalNum, \n");
            sqlstr.append("t.DEAL_NUM as dealNum, \n");
            sqlstr.append("t.SUCESS_NUM as sucessNum, \n");
            sqlstr.append("t.FAIL_NUM as failNum, \n");
            sqlstr.append("t.IGNORE_NUM as ignoreNum \n");
            sqlstr.append("from tl_b_deallog t where t.DEAL_ID = ?");
        
            QueryRunner qr = new QueryRunner(ds);

            List<DealLog> dealLogList =qr.query(sqlstr.toString(), 
            		new BeanListHandler<DealLog>(DealLog.class), dealId);
            if(dealLogList==null||dealLogList.isEmpty()){
                return null;
            }
            return dealLogList.get(0);
        } catch (Exception e){
            logger.error("sql execute err!, {}", e.getMessage());
            return null;
        }
	}
	
	/**
	 * 获取有效账户的所有省份
	 * @return
	 */
	public static Set<String> getAllProvList() {
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYCAT_CRM);
            
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select DISTINCT t.PROVINCE_CODE as provinceCode \n");
            sqlstr.append("from TF_F_ACCT t where t.REMOVE_TAG = 0");
             
            QueryRunner qr = new QueryRunner(ds);

            List<OutAcctTaskBean> list =qr.query(sqlstr.toString(), 
            		new BeanListHandler<OutAcctTaskBean>(OutAcctTaskBean.class));
            if(list==null||list.isEmpty()){
                return null;
            }
    		Set<String> provSet = new HashSet<>();
    		if(ListUtil.isNotEmpty(list)){
    			for(OutAcctTaskBean bean : list){
    				provSet.add(bean.getProvinceCode());
    			}
    		}
    		return provSet;
        } catch (Exception e){
        	logger.error("sql execute err!, {}", e.getMessage());
            return null;
        } 
	}
	
	/**
	 * 通过省份获取有效账户列表
	 */
	public static List<String> getAcctListByProvCode(final String provinceCode) {
		List<String> acctList = new ArrayList<>();
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYCAT_CRM);
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select t.ACCT_ID as acctId from TF_F_ACCT t \n");
            sqlstr.append("where t.REMOVE_TAG = 0 and t.PROVINCE_CODE = ?");
             
            QueryRunner qr = new QueryRunner(ds);
            List<OutAcctTaskBean> list =qr.query(sqlstr.toString(), 
            		new BeanListHandler<OutAcctTaskBean>(OutAcctTaskBean.class), provinceCode);
            if(ListUtil.isNotEmpty(list)){
    			acctList.addAll(list.stream().map(OutAcctTaskBean::getAcctId).collect(Collectors.toList()));
    		}
        } catch (Exception e){
        	logger.error("sql execute err!, {}", e.getMessage());
            return null;
        } 
		return acctList;
	}
	
	public static List<OutAcctTaskBean> getProvCodeByAcctId(final List<String> acctIdList) {
		if(acctIdList==null){
			return Collections.emptyList();
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<acctIdList.size(); i++){
			sb.append("'").append(acctIdList.get(i)).append("',");
		}
		String acctIdStr = sb.substring(0, sb.length()-1);
		
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYCAT_CRM);
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select DISTINCT t.ACCT_ID as acctId, \n");
            sqlstr.append("t.PROVINCE_CODE as provinceCode \n");
            sqlstr.append("from TF_F_ACCT t \n");
            sqlstr.append("where t.REMOVE_TAG = 0 and acct_id in (");
            sqlstr.append(acctIdStr);
            sqlstr.append(")");
            
            QueryRunner qr = new QueryRunner(ds);
            
            List<OutAcctTaskBean> list =qr.query(sqlstr.toString(), 
            		new BeanListHandler<OutAcctTaskBean>(OutAcctTaskBean.class));
            return list;
        } catch (Exception e){
            logger.error("sql execute err!");
            return null;
        } 
	}
	
	/**
	 * 根据省份获取出账的账户总数
	 */
	public static int getAcctTotalByProvCode(final Set<String> provinceSet) {
		if(provinceSet==null || provinceSet.isEmpty()){
			return 0;
		}
		StringBuffer sb = new StringBuffer();
		for(String provinceCode : provinceSet){
			sb.append(provinceCode).append(",");
			sb.append("'").append(provinceCode).append("',");
		}
		String provinceStr = sb.substring(0, sb.length()-1);
		
		int total = 0;
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYCAT_CRM);
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select count(1) as totalNum from TF_F_ACCT t \n");
            sqlstr.append("where t.REMOVE_TAG = 0 and t.PROVINCE_CODE in (");
            sqlstr.append(provinceStr);
            sqlstr.append(")");
            
            QueryRunner qr = new QueryRunner(ds);
            List<OutAcctTaskBean> list =qr.query(sqlstr.toString(), 
            		new BeanListHandler<OutAcctTaskBean>(OutAcctTaskBean.class));
            if(ListUtil.isNotEmpty(list)){
    			total = list.get(0).getTotalNum();
    		}
        } catch (Exception e){
        	logger.error("sql execute err!, {}", e.getMessage());
            return 0;
        } 
		return total;
	}
	
	/**
	 * 初始化出账进度表信息
	 */
	public static boolean insertDealLog(final long dealId, final int totalNum) {
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
            
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("INSERT INTO TL_B_DEALLOG(DEAL_ID,TOTAL_NUM,DEAL_NUM,SUCESS_NUM,\n");
            sqlstr.append("FAIL_NUM,IGNORE_NUM,MQ_CREATE_TIME,UPDATE_TIME)\n");
            sqlstr.append("VALUES (?, ?, 0, 0, 0, 0, SYSDATE(), SYSDATE())");
        
            QueryRunner qr = new QueryRunner(ds);
            qr.insert(sqlstr.toString(), new BeanListHandler<DealLog>(DealLog.class), dealId, totalNum);
            return true;
        } catch (Exception e){
        	logger.error("sql execute err!, {}", e.getMessage());
            return false;
        } 
	}
}
