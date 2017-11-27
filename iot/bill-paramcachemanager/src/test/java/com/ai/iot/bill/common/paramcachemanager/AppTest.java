package com.ai.iot.bill.common.paramcachemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheAB;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheConfigure;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheManager;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheManagerConfigure;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheManagerConfigure.ConfigureEnum;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbClient4Param;
import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.rate_plan.PmRatePlan;
import com.ai.iot.bill.common.paramcachemanager.pog.sample.PoSample;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.DateUtil;

import junit.framework.TestCase;

public class AppTest  extends TestCase{
	private final static Logger logger = LoggerFactory.getLogger(AppTest.class);
	private final static int serializeType=1;
	public class AppThread implements Callable<Boolean>{
		private final Logger logger = LoggerFactory.getLogger(AppThread.class);
		private boolean result=true;
		ParamCacheManager manager;
		private ParamClient client;
		private List<PoBase> dataList;
		private PoSample ps;
		private int j=0;
		private final long defaultVersion=20170720122120L;
		private final long updateVersion=20990731235959L;
		//@SuppressWarnings("deprecation")
		private void init() throws Exception {
			//初始化参数管理组件，并获取客户端对象
			ParamCacheManagerConfigure.initialize(ConfigureEnum.PROPERTIES_FILE);
			ParamCacheManagerConfigure.getGlobalInstance().init();
			logger.info("configure-info={}",ParamCacheManagerConfigure.getGlobalInstance().toString());
			ParamCacheConfigure configure = ParamCacheManagerConfigure.getGlobalInstance();
			configure.setSerializeType(AppTest.serializeType);
			configure .addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName());
			configure .addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_RATE_PLAN.getName());
            manager = ParamCacheManager.getGlobalInstance();
			manager.initialize(configure);
			client = manager.getInstanceClient();
			client.register();
		}
		private void refreshData(long nowYYYYMMDDHHMISS,List<PoBase> poDataList) {
			Map<String,PoGroupRegister>  poGroupMap= PoGroupRegisterFactory.getAllPoGroupObjects();
			Iterator<Entry<String,PoGroupRegister>> iterPoGroup=poGroupMap.entrySet().iterator();
			Map.Entry<String, PoGroupRegister> entryPoGroup;
			Iterator<Entry<String,PoBase>> iterPo;
			Map.Entry<String, PoBase> entryPo;
			long mdbVersion,mdbPoDataSize;
			
			//300+'POG'
			//MdbParamDataWrapper.delPoGroupNames();
			MdbParamDataWrapper.setPoGroupNames(poGroupMap.keySet());
			
			while(iterPoGroup.hasNext()) {
				entryPoGroup=iterPoGroup.next();
				//只有pogsample的数据才清理
				if(!entryPoGroup.getKey().equals(PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName()))
				    continue;
				//po组信息
				MdbParamDataWrapper.ParamGroup pg;
				pg=MdbParamDataWrapper.getPoGroupInfo(entryPoGroup.getKey());
				if(CheckNull.isNull(pg)) {
					mdbVersion=0L;
				}else {
					mdbVersion=pg.getCurrentVersion();
				}
				pg=new MdbParamDataWrapper.ParamGroup();
      			pg.setCurrentVersion(Long.valueOf(nowYYYYMMDDHHMISS).longValue());
      			pg.setLastVersion(mdbVersion);
      			MdbParamDataWrapper.delPoGroupInfo(entryPoGroup.getKey());
      			MdbParamDataWrapper.setPoGroupInfo(entryPoGroup.getKey(),pg);
      			
      			iterPo=entryPoGroup.getValue().getAllPoObjects().entrySet().iterator();
      			while (iterPo.hasNext()) {
      				entryPo=iterPo.next();
	      			//302+PO组名+PO名+更新时间戳+'SIZE'
      				//303+PO组名+PO名+更新时间戳+<N>
      				//304+PO组名+PO名+更新时间戳+'INDEX'
      				//先删除数据
      				mdbPoDataSize=MdbParamDataWrapper.getPoDataSize(entryPoGroup.getKey(), entryPo.getKey(), mdbVersion);
      				for(j=1;j<=mdbPoDataSize;j++) {
      					MdbParamDataWrapper.delPoData(entryPoGroup.getKey(), entryPo.getKey(), mdbVersion, j);
      				}
      				MdbParamDataWrapper.delPoDataSize(entryPoGroup.getKey(), entryPo.getKey(), mdbVersion);
      				MdbParamDataWrapper.delPoIndexNames(entryPoGroup.getKey(), entryPo.getKey(), mdbVersion);
      				//加入数据
      				if(entryPoGroup.getKey().equals(PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName())) {
	      				MdbParamDataWrapper.setPoDataSize(entryPoGroup.getKey(), entryPo.getKey(), nowYYYYMMDDHHMISS, poDataList.size());
	      				int n=0;
		      			for(PoBase o:poDataList) {
		      				n++;
	      					MdbParamDataWrapper.setPoData(entryPoGroup.getKey(), entryPo.getKey(), nowYYYYMMDDHHMISS,n, o);
		      			}
      				}else {
	      				MdbParamDataWrapper.setPoDataSize(entryPoGroup.getKey(), entryPo.getKey(), nowYYYYMMDDHHMISS, 0);
		      		}
      				if(PoGroupRegisterFactory.getAllPoGroupIndexMethods().containsKey(entryPoGroup.getKey())&&
      						PoGroupRegisterFactory.getAllPoGroupIndexMethods().get(entryPoGroup.getKey()).containsKey(entryPo.getKey())) {
      					MdbParamDataWrapper.setPoIndexNames(entryPoGroup.getKey(), entryPo.getKey(), nowYYYYMMDDHHMISS,
	      					PoGroupRegisterFactory.getAllPoGroupIndexMethods().get(entryPoGroup.getKey()).get(entryPo.getKey()).keySet());
      				}
      			}
			} 
		}
		private void waitSwitchLocalCache() {
			Map<String,PoGroupRegister>  poGroupMap= PoGroupRegisterFactory.getAllPoGroupObjects();
			Iterator<Entry<String,PoGroupRegister>> iterPoGroup;
			Map.Entry<String, PoGroupRegister> entryPoGroup;
			boolean isUpdateOK=false;
			Map<String,ParamCacheAB> cache=client.getLocalParamGroupCacheABMap();
			
			while(!isUpdateOK) {
				isUpdateOK=true;
				iterPoGroup=poGroupMap.entrySet().iterator();
				
				while(iterPoGroup.hasNext()) {
					entryPoGroup=iterPoGroup.next();
					//只有pogsample的数据才清理
	                if(!entryPoGroup.getKey().equals(PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName()))
	                    continue;
					if(cache.get(entryPoGroup.getKey()).getPoGroupInfo().getCurrentVersion()!=updateVersion) {
						isUpdateOK=false;
					}
				}
				if(!isUpdateOK) {
					try {
						logger.info("waitManagerThreadSwitchLocalCache()...");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						isUpdateOK=true;
					}
				}
			}			
		}
		//PoSample有3条记录: defaultVersion=>POG_SAMPLE\POG_OPERATOR\POG_ERRRR
		private void prepareData() {
			long nowYYYYMMDDHHMISS=defaultVersion;//DateUtil.nowAbsSeconds();
			List<PoBase> poDataList=new ArrayList<PoBase>();
			PoSample p;
			p=new PoSample();p.setTabGroupName("POG_SAMPLE");p.setUpdateFlag(nowYYYYMMDDHHMISS);poDataList.add(p);
			p=new PoSample();p.setTabGroupName("POG_OPERATOR");p.setUpdateFlag(nowYYYYMMDDHHMISS);poDataList.add(p);
			p=new PoSample();p.setTabGroupName("POG_ERRRR");p.setUpdateFlag(nowYYYYMMDDHHMISS);poDataList.add(p);
			refreshData(nowYYYYMMDDHHMISS,poDataList);
		}
		//PoSample有4条记录: updateVersion=>POG_SAMPLE\POG_OPERATOR(2)\POG_ERRRR
		private void updateData() {
			logger.info("==>updateData In Mdb({})...",updateVersion);
			long nowYYYYMMDDHHMISS=updateVersion;//DateUtil.nowAbsSeconds();
			List<PoBase> poDataList=new ArrayList<PoBase>();
			PoSample p;
			p=new PoSample();p.setTabGroupName("POG_SAMPLE");p.setUpdateFlag(nowYYYYMMDDHHMISS);poDataList.add(p);
			p=new PoSample();p.setTabGroupName("POG_OPERATOR");p.setUpdateFlag(nowYYYYMMDDHHMISS);poDataList.add(p);
			p=new PoSample();p.setTabGroupName("POG_OPERATOR");p.setUpdateFlag(DateUtil.nowAbsSeconds());poDataList.add(p);
			p=new PoSample();p.setTabGroupName("POG_ERRRR");p.setUpdateFlag(nowYYYYMMDDHHMISS);poDataList.add(p);
			refreshData(nowYYYYMMDDHHMISS,poDataList);
			waitSwitchLocalCache();//需等待切换成功
		}
		private void testGetAllDatas() {
			///测试用例
			//1)查询样例表的key为abc对应的结果集
			ps=new PoSample();
			logger.info("==>getAllDatas()...");
			dataList = client.getAllDatas(ps.getPoGroupName(),ps.getPoName());
			if (CheckNull.isNull(dataList)) {
				result=false;
				logger.info("==>getAllDatas()...ERROR");
				//continue;
				return;
			}
			logger.info("==>datasize={}",dataList.size());
			j = 0;
			if(dataList.size()!=3)
				result=false;
			for (PoBase data : dataList) {
				logger.info("["+String.valueOf(j+1) + "]==>" + data.toString());
				j++;
				if(!((PoSample)data).getTabGroupName().equals("POG_SAMPLE")&&
						!((PoSample)data).getTabGroupName().equals("POG_OPERATOR")&&
						!((PoSample)data).getTabGroupName().equals("POG_ERRRR")) {
					result=false;
					logger.info("==>getAllDatas()...ERROR");
				}
			}
		}
		private void testGetDatasByKeyFirst() {
			//2)查询样例表的key为POG_SAMPLE对应的结果集
			logger.info("==>testGetDatasByKeyFirst(POG_SAMPLE)...");
			ps=new PoSample();
			ps.setTabGroupName("POG_SAMPLE");
			//ps.setUpdateFlag(Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS)));
			dataList = client.getDatasByKey(ps.getPoGroupName(),ps.getPoName(),PoSample.getIndex1Name(),ps.getIndex1Key());
			if (CheckNull.isNull(dataList)) {
				result=false;
				logger.info("==>testGetDatasByKeyFirst(POG_SAMPLE)...ERROR");
				//continue;
				return;
			}
			//打印获取到的结果集
			j = 0;
			for (PoBase data : dataList) {
				logger.info("["+String.valueOf(j+1) + "]==>" + data.toString());
				j++;
				if(!((PoSample)data).getTabGroupName().equals("POG_SAMPLE")) {
					result=false;
					logger.info("==>testGetDatasByKeyFirst(POG_SAMPLE)...ERROR");
				}
			}
		}
		private void testGetDatasByKeyMiddle(int size) {
			//3)查询样例表的key为POG_OPERATOR对应的结果集
			logger.info("==>testGetDatasByKeyMiddle(POG_OPERATOR)...");
			ps=new PoSample();
			ps.setTabGroupName("POG_OPERATOR");
			//ps.setUpdateFlag(Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS)));
			dataList = client.getDatasByKey(ps.getPoGroupName(),ps.getPoName(),PoSample.getIndex1Name(),ps.getIndex1Key());
			if (CheckNull.isNull(dataList)||dataList.size()!=size) {
				result=false;
				if(CheckNull.isNull(dataList)) {
					logger.info("==>testGetDatasByKeyMiddle(POG_OPERATOR)...ERROR:dataList.size()=0");
				}else {
					logger.info("==>testGetDatasByKeyMiddle(POG_OPERATOR)...ERROR:dataList.size()={}",dataList.size());
				}
				//continue;
				return;
			}
			//打印获取到的结果集
			j = 0;
			for (PoBase data : dataList) {
				logger.info("["+String.valueOf(j+1) + "]==>" + data.toString());
				j++;
				if(!((PoSample)data).getTabGroupName().equals("POG_OPERATOR")) {
					logger.info("==>testGetDatasByKeyMiddle2(POG_OPERATOR)...ERROR");
					result=false;
				}
			}
		}
		private void testGetDatasByKeyNoExist() {
			//4)查询样例表的key为POG_ERRRR对应的结果集
			logger.info("==>testGetDatasByKeyNoExist(POG_OOOOO)...");
			ps=new PoSample();
			ps.setTabGroupName("POG_OOOOO");
			//ps.setUpdateFlag(Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS)));
			dataList = client.getDatasByKey(ps.getPoGroupName(),ps.getPoName(),PoSample.getIndex1Name(),ps.getIndex1Key());
			if (!CheckNull.isNull(dataList)) {
				result=false;
				logger.info("==>testGetDatasByKeyNoExist(POG_OOOOO)...ERROR");
				//continue;
				return;
			}
		}
		private void testGetDatasByKeyMultiRecs(long v) {
			//4)查询样例表的key为POG_ERRRR对应的结果集
			logger.info("==>testGetDatasByKeyMultiRecs({})...",v);
			ps=new PoSample();
			ps.setUpdateFlag(v);
			//ps.setUpdateFlag(Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS)));
			dataList = client.getDatasByKey(ps.getPoGroupName(),ps.getPoName(),PoSample.getIndex2Name(),ps.getIndex2Key());
			if (CheckNull.isNull(dataList)||dataList.size()!=3) {
				result=false;
				if(CheckNull.isNull(dataList)) {
					logger.info("==>testGetDatasByKeyMultiRecs({})...ERROR:dataList.size()=0",v);
				}else {
					logger.info("==>testGetDatasByKeyMultiRecs({})...ERROR:dataList.size()={}",v,dataList.size());
				}
				//continue;
				return;
			}
			//打印获取到的结果集
			j = 0;
			for (PoBase data : dataList) {
				logger.info("["+String.valueOf(j+1) + "]==>" + data.toString());
				j++;
				if(!(((PoSample)data).getUpdateFlag()==v)) {
					logger.info("==>testGetDatasByKeyMultiRecs({})...ERROR",v);
					result=false;
				}
			}
		}
		public void run() {
			//模拟业务线程
			try {
				init();
			} catch (Exception e1) {
				result=false;
				e1.printStackTrace();
			}
			//...
			boolean idle = false;
			long now=DateUtil.nowAbsSeconds();
			for (int i = 0; ; i++) {
				if(DateUtil.nowAbsSeconds()-now>60*2)//运行2分钟
					break;
				else {
					idle = (i % 2 == 0 ? false : true);
					if (idle) {//空闲处理
						client.heartBeat();
						try {
							logger.info("sleep...");
							Thread.sleep(3000);
						} catch (InterruptedException e) {
						}
						continue;
					}					
				}				
				//忙时处理事件开始
				client.beginTrans();//开始事务
				//...
				
				///静态测试用例
				testGetAllDatas();
				testGetDatasByKeyFirst();
				testGetDatasByKeyMiddle(1);
				testGetDatasByKeyNoExist();		
				testGetDatasByKeyMultiRecs(defaultVersion);
				//切换的测试用例
				updateData();
				testGetDatasByKeyMiddle(2);
				testGetDatasByKeyMultiRecs(updateVersion);
				//...
				PmRatePlan prp=new PmRatePlan(client);
				int planId=20060002;
				List<?> resultlist=prp.getPoRatePlanByPlanId(planId);
				if(resultlist!=null&&resultlist.size() > 0)
				    logger.info("getPoRatePlanByPlanId("+String.valueOf(planId)+")...OK");
				else
				    logger.info("getPoRatePlanByPlanId("+String.valueOf(planId)+")...FAILURE");
				//当前事件处理结束
				client.endTrans();
				break;
				
			}
			//...
//			try {
//				Thread.sleep(3*60*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			result=false;
//			}
			//业务线程退出
			manager.removeClient(client.getClientId());
		}

		@Override
		public Boolean call() throws Exception {
			run();
			return Boolean.valueOf(result);
		}
	}
	public void testInit() throws Exception {
		ParamCacheManagerConfigure.initialize(ConfigureEnum.PROPERTIES_FILE);
		ParamCacheManagerConfigure.getGlobalInstance().init();
		logger.info("configure-info={}",ParamCacheManagerConfigure.getGlobalInstance().toString());
		ParamCacheConfigure configure =ParamCacheManagerConfigure.getGlobalInstance();
		configure.setSerializeType(AppTest.serializeType);
		configure .addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName());
		configure .addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_RATE_PLAN.getName());
        try {
			MdbClient4Param.getGlobalInstance().initialize(configure.getMdbConnectType(),
					configure.getMdbParamHostAndPorts(),
					configure.getMdbParamPassword(),
					configure.isMdbParamUseMaster());
		} catch (POException e) {
			e.printStackTrace();
		}
		PoGroupRegisterFactory.create(configure.getUsedPoGroupNames());
		AppThread app=new AppThread();
		app.prepareData();
	}
	
	public void testAll() {
		boolean result=true;
		try {
			testInit();
 			
			//模拟主线程
			//...
			//ParamCacheManagerConfigure configure = ParamCacheManagerConfigure.getGlobalInstance();
			//ParamCacheManager manager = ParamCacheManager.getGlobalInstance();
			//manager.initialize(configure);
			///////////////////////////////////////////////
			//临时测试
			
			///////////////////////////////////////////////
			//模拟业务线程
			ExecutorService exec = Executors.newCachedThreadPool();//工头
			ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();//
			for(int i = 0 ; i < 1 ;i++){
			    results.add(exec.submit(new AppThread()));//submit返回一个Future，代表了即将要返回的结果
			}
			
			//...
			
			//测试业务线程是否退出
			for(int i = 0 ; i < 1 ;i++){
				if(results.get(i).get().booleanValue()==false)
					result=false;
			}
			
			//主线程退出
			//manager.finalize();
		} catch(Exception e) {
			result=false;
			e.printStackTrace();
		} finally {
			
		}
		logger.info("result={}",result);
		assertTrue(result);
	}

}
