package com.ai.iot.bill.topo.fileTopo;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.common.util.FileUtil;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.common.util.StringUtil;
import com.ai.iot.bill.dao.AcctBill2BssDao;
import com.ai.iot.bill.entity.computebill.AcctBill2Bss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 获取账单数据并生成账单文件
 * 
 * Created by zhaojiajun on 2017/8/17.
 */
public class AcctBillFileBolt implements IRichBolt {

	private static final long serialVersionUID = -8578330524495403499L;
	private static final Logger logger = LoggerFactory.getLogger(AcctBillFileBolt.class);
	private OutputCollector collector;
	//账单文件字段分隔符
	private static final String FIELD_SPLIT = ",";
	//账单文件行分隔符
	private static final String LINE_SPLIT = "\n";
	//本账期账单文件生成结束标志
	private static final String END_FLAG = "-1";
	private String tmpPath;
	private String filePath;

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		this.tmpPath = this.checkFilePath((String) stormConf.get("tmpPath"));
		this.filePath = this.checkFilePath((String) stormConf.get("filePath"));
		if(StringUtil.isEmpty(tmpPath)){
			logger.error("Acct File tmp dir is empty");
			return;
		}
		if(StringUtil.isEmpty(filePath)){
			logger.error("Acct File dir is empty");
			return;
		}
	}

	public void execute(Tuple input) {
		// 数据处理
		int cycleId = input.getIntegerByField("cycleId");
		String provCode = input.getStringByField("provCode");
		
		if(END_FLAG.equals(provCode)){
			//本账期账单生成结束，从临时目录移动文件到正式目录
			boolean bl = FileUtil.moveFolderFile(tmpPath, filePath);
			if(bl){
				this.collector.ack(input);
			} else {
				this.collector.fail(input);
				logger.warn("The bill file moved from the temporary directory to the official directory failed");
			}
		} else {
			//根据账期和省份获取账单数据列表
			List<AcctBill2Bss> list = AcctBill2BssDao.getAcctBill2Bss(cycleId, provCode);
			if(ListUtil.isNotEmpty(list)){
				StringBuilder sb = new StringBuilder();
				for(AcctBill2Bss acctBill2Bss : list){
					sb.append(acctBill2Bss.getCityCode()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getProvCode()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getAreaCode()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getOperAcctId()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getBillType()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getCycleId()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getHeadquarterItemId()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getProvItemId()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getFee()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getOriginalFee()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getDiscountFee()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getBillStartDate()).append(FIELD_SPLIT);
					sb.append(acctBill2Bss.getBillEndDate()).append(LINE_SPLIT);
				}
				
				StringBuilder fileName = new StringBuilder();
				fileName.append(this.tmpPath);
				//拼接话单文件名
				fileName.append("CBSS_DMEMBILL_").append(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD)).append("_");
				fileName.append(provCode);
				fileName.append("9001").append(".res");
				String tmpFileName = fileName.toString();
				File file = new File(tmpFileName);
				if(FileUtil.delFile(tmpFileName)){
					//向账单文件尾部增量写入数据
					boolean bf = FileUtil.writeFile(sb.toString(), file, true);
					if(bf){
						this.collector.ack(input);
					} else {
						this.collector.fail(input);
						logger.warn("The data was written to the billing file failed, {}", FileUtil.getMessage());
					}
				} else {
					this.collector.fail(input);
					logger.warn("Failed to delete old billing file, {}", FileUtil.getMessage());
				}
			}
		}
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//do nothing
	}

	@Override
	public void cleanup() {
		//do nothing
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		//do nothing
		return null;
	}
	
	private String checkFilePath(String filePath){
		if(StringUtil.isEmpty(filePath)){
			return null;
		}
		File file = new File(filePath);
		if(file.canRead()){
			return filePath;
		}
		if(!file.mkdirs()){
			logger.error("mkdir cdr path is failure : {}", filePath);
			return null;
		}
		return filePath;
	}
}
