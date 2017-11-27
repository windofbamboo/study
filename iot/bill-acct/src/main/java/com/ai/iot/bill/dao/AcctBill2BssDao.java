package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.param.ParamBean;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.entity.computebill.AcctBill2Bss;
import com.ai.iot.bill.entity.param.CycleBean;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取账单数据
 * Created by zhaojiajun on 2017/8/17.
 */
public class AcctBill2BssDao {

	private static final Logger logger = LoggerFactory.getLogger(AcctBill2BssDao.class);
	
	/**
	 * 获取配置参数
	 * @param paramType
	 * @return
	 */
	public static List<ParamBean> getParamBeanList(final String paramType) {
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
            
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select t.param_name as paramName,\n");
            sqlstr.append("t.param_value as paramValue,\n");
            sqlstr.append("t.param_value2 as paramValue2 \n");
            sqlstr.append("from td_b_param t where t.param_type = ?");
            
            QueryRunner qr = new QueryRunner(ds);

            List<ParamBean> paramBeanList =qr.query(sqlstr.toString(), 
            		new BeanListHandler<ParamBean>(ParamBean.class), paramType);
            return paramBeanList;
        } catch (Exception e){
            logger.error("sql execute err!");
            return null;
        } 
	}
	
	/**
	 * 根据账期ID获取省份编码
	 * @param cycleId
	 * @return
	 */
	public static List<String> getProvCodeByCycleId(final int cycleId) {
		DataSource ds = null;
		List<String> provCodeList = new ArrayList<>();
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYCAT_CRM);
            String month = String.valueOf(cycleId).substring(4, 6);
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select DISTINCT t.PROV_CODE as provCode \n");
            sqlstr.append("from TS_BSS_BILL_");
            sqlstr.append(month);
            sqlstr.append(" t where t.CYCLE_ID = ?");
            
            QueryRunner qr = new QueryRunner(ds);
            List<AcctBill2Bss> billList =qr.query(sqlstr.toString(), 
            		new BeanListHandler<AcctBill2Bss>(AcctBill2Bss.class), cycleId);
            if(ListUtil.isNotEmpty(billList)){
				provCodeList.addAll(billList.stream()
						.filter(bill -> bill != null).map(AcctBill2Bss::getProvCode).collect(Collectors.toList()));
			}
            return provCodeList;
        } catch (Exception e){
            logger.error("sql execute err!");
            return null;
        } 
	}
	
	/**
	 * 根据根据状态获取账期
	 * @param status
	 * @return
	 */
	public static List<Integer> getCycleIdByStatus(final int status) {
		DataSource ds = null;
		List<Integer> cycleIdList = new ArrayList<>();
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select t.CYCLE_ID as cycleId \n");
            sqlstr.append("from TD_B_CYCLE t where t.MONTH_ACCT_STATUS = ? \n");
            sqlstr.append("order by t.CYCLE_ID");
            
            QueryRunner qr = new QueryRunner(ds);
            List<CycleBean> cycleList =qr.query(sqlstr.toString(), 
            		new BeanListHandler<CycleBean>(CycleBean.class), status);
            if(ListUtil.isNotEmpty(cycleList)){
    			cycleIdList.addAll(cycleList.stream().filter(cycleBean -> cycleBean != null)
    					.map(CycleBean::getCycleId).collect(Collectors.toList()));
    		}
            return cycleIdList;
        } catch (Exception e){
            logger.error("sql execute err!");
            return null;
        } 
	}
	
	/**
	 * 获取账单数据
	 */
	public static List<AcctBill2Bss> getAcctBill2Bss(final int cycleId, final String provCode) {
		DataSource ds = null;
		try {
			String month = String.valueOf(cycleId).substring(4, 6);
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYCAT_CRM);
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("select t.CITY_CODE as cityCode, \n");
            sqlstr.append("t.PROV_CODE as provCode, \n");
            sqlstr.append("t.AREA_CODE as areaCode, \n");
            sqlstr.append("t.OPER_ACCT_ID as operAcctId, \n");
            sqlstr.append("t.ACCT_ID as acctId, \n");
            sqlstr.append("t.BILL_TYPE as billType, \n");
            sqlstr.append("t.CYCLE_ID as cycleId, \n");
            sqlstr.append("t.HEADQUARTER_ITEM_ID as headquarterItemId, \n");
            sqlstr.append("t.PROV_ITEM_ID as provItemId, \n");
            sqlstr.append("t.FEE as fee, \n");
            sqlstr.append("t.ORIGINAL_FEE as originalFee, \n");
            sqlstr.append("t.DISCOUNT_FEE as discountFee, \n");
            sqlstr.append("t.BILL_START_DATE as billStartDate, \n");
            sqlstr.append("t.BILL_END_DATE as billEndDate \n");
            sqlstr.append("from TS_BSS_BILL_");
            sqlstr.append(month);
            sqlstr.append(" t where t.CYCLE_ID = ? and t.PROV_CODE = ? ");
            
            QueryRunner qr = new QueryRunner(ds);
            List<AcctBill2Bss> billList =qr.query(sqlstr.toString(), 
            		new BeanListHandler<AcctBill2Bss>(AcctBill2Bss.class), cycleId, provCode);
            return billList;
        } catch (Exception e){
            logger.error("sql execute err!");
            return null;
        } 
	}
	
	
	/**
	 * 更新出账进度
	 * @param status
	 * @return
	 */
	public static boolean updateMonthAcctStatus(final int status, final int cycleId) {
		DataSource ds = null;
		try {
            ds= DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
            StringBuilder sqlstr = new StringBuilder();
            sqlstr.append("update TD_B_CYCLE t set \n");
            sqlstr.append("t.MONTH_ACCT_STATUS = ?, \n");
            sqlstr.append("t.UPDATE_TIME=DATE_FORMAT(NOW(),'%Y%m%d%H%i%S'), \n");
            sqlstr.append("t.UPDATE_PERSON='bill2bss' \n");
            sqlstr.append("where t.CYCLE_ID = ? \n");
            
            QueryRunner qr = new QueryRunner(ds);
            qr.update(sqlstr.toString(), status, cycleId);
            return true;
        } catch (Exception e){
            logger.error("sql execute err!");
            return false;
        } 
	}
}
