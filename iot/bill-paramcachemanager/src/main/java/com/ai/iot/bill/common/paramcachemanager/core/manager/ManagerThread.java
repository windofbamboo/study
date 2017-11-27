package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.DateUtil;

/**
 * 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [参数管理线程实现类] 
 * 1）负责和MDB间的更新； 
 * 2）负责和各个客户端间的交互切换；
 * 3）设计AB两个内存对象进行更新管理，更新的时候不影响业务中断 
 * 4）如果AB两个内存对象都在忙（有线程在访问），则进入等待期
 * 5）定时删除
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 */
public class ManagerThread implements Runnable {
	/** 线程状态*/
	public enum StatusEnum{
		RUN_INITTED,RUN_ALREADY,RUN_EXIT
	}
	/** 线程停止标记 */
	private volatile boolean stop = false;
	
	private volatile StatusEnum status = StatusEnum.RUN_INITTED;
	
	private volatile String exitErrorMessage="";

    private static final Logger logger = LoggerFactory.getLogger(ManagerThread.class);

	/** 所有的PO组集 */
	private LocalParamGroupLoaderMap localParamGroupLoaderMap=new LocalParamGroupLoaderMap();

	public LocalParamGroupLoaderMap getLocalParamGroupLoaderMap() {
		return localParamGroupLoaderMap;
	}

	/// 上次上报信息时间，记录到绝对秒即可
	private long lastReportTime;

	/** 主流程
	 * 
	 * */
	public void run() {
		status=StatusEnum.RUN_INITTED;
		logger.info("Start Thread ...");
		try {
			register();
		} catch (Exception e) {
			//e1.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
			setExitErrorMessage(e);
			stop=true;
			status=StatusEnum.RUN_EXIT;
			unRegister();
			logger.info("Exit Thread ...");
			return;
		}
		status=StatusEnum.RUN_ALREADY;
		while (!stop) {
			///删除老数据（保留最新的两份）
			localParamGroupLoaderMap.cleanOldDataInMdb();
			try {
				if(!localParamGroupLoaderMap.isUpdate()) {
					Thread.sleep(ParamCacheManagerConfigure.getGlobalInstance().getScanSleepSeconds()*1000L);
					///定时汇报实例信息
					reportFixTime();
					continue;
				}
				///1）从MDB更新数据
				localParamGroupLoaderMap.update();
				///2）汇报本进程实例信息
				logger.info("------------------report commit PoGroupCacheInfos to Mdb-----------------------");
				//PARAM_INS_GROUP_VISITOR（37000）,PARAM_INS_GROUP_VISITOR_INFO（37100）
		    	localParamGroupLoaderMap.reportClientCacheInfo();
		    	//PARAM_INS_GROUP（35100）
		    	//PARAM_INS_GROUP_PO_DATA_SIZE（35200）
		    	//PARAM_INS_GROUP_PO_INDEX（35400）
				localParamGroupLoaderMap.reportProcCacheInfo();
				logger.info("------------------dump last version--------------------------------------------------");
				localParamGroupLoaderMap.dumpLastVersion();
				logger.info("------------------dump current version--------------------------------------------------");
				localParamGroupLoaderMap.dump();
			} catch (InterruptedException e) {// 睡眠中止
			     // 恢复中断状态
				Thread.currentThread().interrupt();
			    if(stop) {
			        break;
			    }
			}catch (Exception e) {
				logger.error(e.getLocalizedMessage(),e);
				setExitErrorMessage(e);
				reportFixTime();
				continue;
			}
			///定时汇报实例信息，需放在后面执行，以便第一次时加载过数据了
			reportFixTime();
		}
		unRegister();
		logger.info("Thread Already Exit!");
		this.exitErrorMessage="Thread Already Exit!";
		status=StatusEnum.RUN_EXIT;
	}

	/**
	 * 1）从MDB进行全量更新一次
	 * 2）在MDB注册进程实例信息 
	 * @throws Exception 
	 */
	private void register() throws Exception {
		///获取所有的PO组列表
		localParamGroupLoaderMap.loaderPoGroupObjects();
		///获取所有的PO组数据
		localParamGroupLoaderMap.updateAll();
		///在MDB注册进程实例信息 
		reportFixTime();
		logger.info("------------------dump current version--------------------------------------------------");
		localParamGroupLoaderMap.dump();
		return;
	}

	/** 注销所有访问者实例信息和进程实例信息*/
	private void unRegister() {
		if (!CheckNull.isNull(localParamGroupLoaderMap)) {
			logger.info("unRegister()...");
			localParamGroupLoaderMap.unLoaderPoGroupDatas();
		}
	}

	/** 定时汇报本进程实例信息和客户端访问者实例信息 */
	private void reportFixTime() {
		long diffSeconds = 0;
		long now;
		if (CheckNull.isNull(lastReportTime)) {
			diffSeconds = ParamCacheManagerConfigure.getGlobalInstance().getReportFixTimeSeconds();
			now = lastReportTime = DateUtil.nowAbsSeconds();
		} else {
			now = DateUtil.nowAbsSeconds();
			diffSeconds = now - lastReportTime;
		}
		/// 第一次 或 时间差超过1分钟
		if (diffSeconds >= ParamCacheManagerConfigure.getGlobalInstance().getReportFixTimeSeconds()) {
			logger.info("------------------FixTimely report all PoGroupCacheInfos to MDB-----------------------");
			localParamGroupLoaderMap.reportAllClientCacheInfo();
			localParamGroupLoaderMap.reportProcCacheInfo();
			lastReportTime = now;
		}
	}

	/** */
	public void stop() {
		stop = true;
	}

	/** 线程是否准备ok*/
	public boolean isOk() {
		return status==StatusEnum.RUN_ALREADY;
	}
	
	/** 线程是否准备ok，没ok就等待*/
	public boolean waitOk() {
		while(!isOk()) {
			if(status==StatusEnum.RUN_EXIT) {
				logger.error("thread instance is NONE!");
				return false;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("{}",e);
				// 恢复中断状态
                Thread.currentThread().interrupt();
				return false;
			}
		}
		return true;
	}

    public String getExitErrorMessage() {
        return exitErrorMessage;
    }

    private void setExitErrorMessage(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter ps=new PrintWriter(sw);
        e.printStackTrace(ps);
        this.exitErrorMessage = e.getLocalizedMessage()+"\n"+sw.toString();
    }

/*	protected void finalize() {
		stop();
	}*/
}
