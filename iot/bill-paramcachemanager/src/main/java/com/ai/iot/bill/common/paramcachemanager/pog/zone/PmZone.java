package com.ai.iot.bill.common.paramcachemanager.pog.zone;

import java.util.List;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PmBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoApn;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoDataStreamGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoProvider;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoZone;
import com.ai.iot.bill.common.util.CheckNull;

/**
 * 区域PM
 * @author xue
 *
 */
public class PmZone extends PmBase {

	private PoZone poZone = new PoZone();

	private PoApn poApn = new PoApn();
	private PoProvider poProvider = new PoProvider();
	private PoDataStreamGroup poDataStreamGroup = new PoDataStreamGroup();

	public PmZone(ParamClient paramClient) {
		super(paramClient);
	}

	/**
	 * 根据区域ID读取区域参数
	 * @param zoneId
	 * @return
	 */
	public List<PoBase> getPoZoneByZoneId(int zoneId) {
		poZone.setZoneId(zoneId);
		return paramClient.getDatasByKey(poZone.getPoGroupName(), poZone.getPoName(), PoZone.getIndex1Name(),
				poZone.getIndex1Key());
	}

	
	/**
	 * 读取所有的APN参数
	 * @return
	 */
	public List<PoBase> getPoApn() {
		return paramClient.getAllDatas(poApn.getPoGroupName(), poApn.getPoName());
	}
	
	/**
	 * 根据apn编码获取对应的apn id
	 * @param apnCode
	 * @return
	 */
	public int getApnId(String apnName){
		if(CheckNull.isNull(apnName)) {
			return 0;
		}
		poApn.setApnName(apnName);
		List<PoBase> poApns = paramClient.getDatasByKey(poApn.getPoGroupName(), poApn.getPoName(), PoApn.getIndex2Name(), poApn.getIndex2Key());
		if(!CheckNull.isNull(poApns)){
			return ((PoApn)poApns.get(0)).getApnId();
		}
		return 0;
	}
	
	/**
	 * 根据运营商编码获取运营商id
	 * @param providerCode
	 * @return
	 */
	public int getProviderId(String providerCode) {
		if(providerCode == null) {
			return 0;
		}
		poProvider.setProviderCode(providerCode);
		List<PoBase> providers =  paramClient.getDatasByKey(poProvider.getPoGroupName(), poProvider.getPoName(),
				PoProvider.getIndex2Name(), poProvider.getIndex2Key());
		if(!CheckNull.isNull(providers)){
			return ((PoProvider)providers.get(0)).getProviderId();
		}
		return 0;
	}

	/**
	 * 读取所有的运营商
	 * @return
	 */
	public List<PoBase> getPoProvider() {
		return paramClient.getAllDatas(poProvider.getPoGroupName(), poProvider.getPoName());
	}
	
	
	/**
	 * 根据数据流编码获得数据流ID
	 * @param dataStreamsCode
	 * @return
	 */
	public int getDataSreamGroupId(String dataStreamsCode) {
		if(dataStreamsCode == null) {
			return 0;
		}
		poDataStreamGroup.setDataStreamsCode(dataStreamsCode);
		List<PoBase> poDataStreamGroups =  paramClient.getDatasByKey(poDataStreamGroup.getPoGroupName(), poDataStreamGroup.getPoName(),
				PoDataStreamGroup.getIndex2Name(), poDataStreamGroup.getIndex2Key());
		if(!CheckNull.isNull(poDataStreamGroups)){
			return ((PoDataStreamGroup)poDataStreamGroups.get(0)).getDataStreamsId();
		}
		return 0;
	}
	
	/**
	 * 读取所有的数据流
	 * @return
	 */
	public List<PoBase> getPoDataSreamGroup() {
		return paramClient.getAllDatas(poDataStreamGroup.getPoGroupName(), poDataStreamGroup.getPoName());
	}
	
	/**
	 * 根据运营商ID读取运营商编码参数
	 * @param providerId
	 * @return
	 */
	public PoBase getPoProviderById(int providerId) {
		poProvider.setProviderId(providerId);
		List<PoBase> providerList = paramClient.getDatasByKey(poProvider.getPoGroupName(), poProvider.getPoName(), PoProvider.getIndex1Name(),
				poProvider.getIndex1Key());
		if (!CheckNull.isNull(providerList)) {
			return providerList.get(0);
		}
		return null;
	}
	
	/**
	 * 根据IMSI求取运营商
	 * @param imsi
	 * @return
	 */
	public PoBase getProviderByImsi(String imsi) {
		poProvider.setProviderImsi(imsi);
		List<PoBase> providerList = paramClient.getDatasByKey(poProvider.getPoGroupName(), poProvider.getPoName(), PoProvider.getIndex3Name(),
				poProvider.getIndex3Key());
		if (!CheckNull.isNull(providerList)) {
			return providerList.get(0);
		}
		return null;
	}
	
	public PoBase getProviderByMscCode(String mscCode) {
		poProvider.setMscCode(mscCode);
		List<PoBase> providerList = paramClient.getDatasByKey(poProvider.getPoGroupName(), poProvider.getPoName(), PoProvider.getIndex4Name(),
				poProvider.getIndex4Key());
		if (!CheckNull.isNull(providerList)) {
			return providerList.get(0);
		}
		return null;
	}
}
