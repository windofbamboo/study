package com.ai.iot.bill.common.paramcachemanager.core.pobase;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.util.DateUtil;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [Po数据存储类]
 * 一个PO的数据，可以看作是一个表的数据
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * 
 * */
public class PoContainer {
	private final static Logger logger = LoggerFactory.getLogger(PoContainer.class);
	
    /** 存放PO数据*/
    private List<PoBase> poDataList=new LinkedList<PoBase>();
    
    /**存放po对应的基本信息，和pobase复用了*/
    PoBase poBaseInfo;
    
    @SuppressWarnings("unused")
	private PoContainer() {}
    
    public PoContainer(PoBase poBase) {
    	this.poBaseInfo=poBase;
    }
    
    ///重置数据和索引
    public void resetPoContainer() {
    	poDataList.clear();
    }
    
    /** 获取数据*/
    public List<PoBase> getPoDataList() {
    	return this.poDataList;
    }
    
    /**
     * 获取PO基本信息
     * */
    public PoBase getPoBase() {
       return this.poBaseInfo;
    }
    
    ///获取每一个po的数据
    public boolean setPoDataFromMDB(long version) {
		long poDataSize=MdbParamDataWrapper.getPoDataSize(poBaseInfo.getPoGroupName(), poBaseInfo.getPoName(), version);
		PoBase poBase;
		Date beginDate=new Date();
		
		//logger.info("{}->setPoDataFromMDB({}.{}) : datasize={}",DateUtil.getCurrentDateTime(beginDate,DateUtil.SEG_YYYYMMDD_HHMMSS),
		//					poBaseInfo.getPoGroupName(),poBaseInfo.getPoName(),poDataSize);
		for(int n=1;n<=poDataSize;n++) {
			poBase=MdbParamDataWrapper.getPoData(poBaseInfo.getPoGroupName(), poBaseInfo.getPoName(), version, n);
			//logger.info("setPoDataFromMDB({}) : data[{}]={}",poBaseInfo.getPoName(),n,poBase);
			poDataList.add(poBase);
		}
		logger.info("{}->setPoDataFromMDB({}.{}) : datasize={},total(secs)={},version={}",DateUtil.getCurrentDateTime(beginDate,DateUtil.SEG_YYYYMMDD_HHMMSS),
				poBaseInfo.getPoGroupName(),poBaseInfo.getPoName(),poDataSize,DateUtil.diffSeconds(new Date(),beginDate),version);
		return true;
    }
    
//    /***********************************************************************
//	 * PARAM_INS_GROUP_PO_DATA_SIZE（35200）
//     * ***********************************************************************/
//	public boolean reportPoCacheInfo(long version) {
//			logger.info("reportAllPoCacheInfo({})...",poBaseInfo.getPoGroupName());
//    		//删除数据
//	    	MdbParamDataWrapper.delInsPoDataSize(poBaseInfo.getPoGroupName(), poBaseInfo.getPoName(), version);
//	    	MdbParamDataWrapper.setInsPoDataSize(poBaseInfo.getPoGroupName(), poBaseInfo.getPoName(), version,poDataList.size());
//    	    return true;
//    }
}
