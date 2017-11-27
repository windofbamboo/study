package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.*;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shade.storm.com.google.common.collect.Lists;
import shade.storm.com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class MMDevice extends MMBase {
    private static final Logger logger = LoggerFactory.getLogger(MMDevice.class);
    MdbTables mdbTables;
    private MtDeviceInfo mtDeviceInfo;
    private MtDeviceImsi mtDeviceImsi;
    private MtDeviceMsisdn mtDeviceMsisdn;
    private MtAccount mtAccount;
    private DeviceMo deviceMo;
    private DeviceRatePlanMo deviceRatePlanMo;
    private DeviceOthersMo deviceOthersMo;
    private RenewalRateQueueMo renewalRateQueueMo;
    private DeviceImsiMo deviceImsiMo;
    private DeviceMsisdnMo deviceMsisdnMo;
    private AccountMo accountMo;
    private AccountAppMo accountAppMo;

    public MMDevice() throws MdbCommonException {
        this(true);
    }

    public MMDevice(CustJedisCluster jc) throws MdbCommonException {
        super(jc);
    }

    public MMDevice(boolean isMdbMaster) throws MdbCommonException {
        super(BaseDefine.CONNTYPE_REDIS_DEVINFO, isMdbMaster);
    }

    @Override
    protected void init() {
        mtAccount = new MtAccount();
        mtDeviceInfo = new MtDeviceInfo();
        mtDeviceImsi = new MtDeviceImsi();
        mtDeviceMsisdn = new MtDeviceMsisdn();

        deviceMo = new DeviceMo();
        deviceRatePlanMo = new DeviceRatePlanMo();
        deviceOthersMo = new DeviceOthersMo();
        renewalRateQueueMo = new RenewalRateQueueMo();
        deviceImsiMo = new DeviceImsiMo();
        deviceMsisdnMo = new DeviceMsisdnMo();
        accountMo = new AccountMo();
        accountAppMo = new AccountAppMo();
        mdbTables = new MdbTables(this.mdbClient4Cluster);
        super.addMdbTable(mdbTables, mtAccount);
        super.addMdbTable(mdbTables, mtDeviceInfo);
        super.addMdbTable(mdbTables, mtDeviceImsi);
    }

    /**
     * 根据账户ID查找可测试用量表
     *
     * @param acctId
     * @return
     */
    public AccountAppMo getAcctApplication(long acctId) {
        mtAccount.setAcctId(acctId);
        mdbTables.selectTables(mtAccount);
        @SuppressWarnings("unchecked")
        List<AccountAppMo> accountAppMos = (List<AccountAppMo>) mdbTables.getList(accountAppMo.getField());
        if (CheckNull.isNull(accountAppMos) || accountAppMos.isEmpty()) {
            return null;
        }
        return accountAppMos.get(0);
    }

    /**
     * 根据设备id 获取 设备详情
     *
     * @param deviceId
     * @return
     */
    public DeviceMo getDeviceInfo(long deviceId) {
        mtDeviceInfo.setDeviceId(deviceId);
        mdbTables.selectTables(mtDeviceInfo);
        return (DeviceMo) mdbTables.getData(deviceMo.getField());
    }

    /**
     * 根据设备id 获取 设备资费计划
     *
     * @param deviceId
     * @return
     */
    public DeviceRatePlanMo getDeviceRatePlan(long deviceId) {
        mtDeviceInfo.setDeviceId(deviceId);
        mdbTables.selectTables(mtDeviceInfo);
        return (DeviceRatePlanMo) mdbTables.getData(deviceRatePlanMo.getField());
    }
    
    /**
     * 根据设备id 获取 设备资费计划 列表,同一资费计划id，版本号选择最大的
     *
     * @param deviceId
     * @return
     */
    public List<DeviceRatePlanMo> getDeviceRatePlanListByDeviceId(long deviceId) {
        mtDeviceInfo.setDeviceId(deviceId);
        mdbTables.selectTables(mtDeviceInfo);
        @SuppressWarnings("unchecked")
        List<DeviceRatePlanMo> ratePlanMos = (List<DeviceRatePlanMo>) mdbTables.getList(deviceRatePlanMo.getField());
        if (CollectionUtils.isEmpty(ratePlanMos)) {
            return Lists.newArrayList();
        }
        Map<Integer, DeviceRatePlanMo> map = Maps.newHashMap();
        ratePlanMos.forEach(ratePlan -> {
            int planId = ratePlan.getPlanId();
            int verId = ratePlan.getPlanVersionId();
            DeviceRatePlanMo ratePlanMo = map.get(planId);
            if (ratePlanMo == null) {
                map.put(planId, ratePlan);
                return;
            }
            int matchVerId = ratePlanMo.getPlanVersionId();
            if (matchVerId < verId) {
                map.put(planId, ratePlan);
            }
        });

        List<DeviceRatePlanMo> planMos = Lists.newArrayList();
        Map<Integer, Integer> tempMap = Maps.newHashMap();
        for (DeviceRatePlanMo deviceRatePlanMo : ratePlanMos) {
            int planid = deviceRatePlanMo.getPlanId();
            if (map.containsKey(planid) && !tempMap.containsKey(planid)) {
                planMos.add(map.get(planid));
                tempMap.put(planid, planid);
            }
        }

        return planMos;
    }
    
    /**
     * 根据设备id 获取 设备详情
     *
     * @param deviceId
     * @return
     */
    public DeviceOthersMo getDeviceOthers(long deviceId) {
        mtDeviceInfo.setDeviceId(deviceId);
        mdbTables.selectTables(mtDeviceInfo);
        return (DeviceOthersMo) mdbTables.getData(deviceOthersMo.getField());
    }
    /**
     * 根据设备id 和资费获取 某个续约的资费计划信息
     *
     * @param deviceId
     * @return
     */
    public RenewalRateQueueMo getRenewalRateQueue(long deviceId,long ratePlanId,long eventTime) {
        mtDeviceInfo.setDeviceId(deviceId);
        mdbTables.selectTables(mtDeviceInfo);
        @SuppressWarnings("unchecked")
        List<RenewalRateQueueMo> renewalRateQueueMos = (List<RenewalRateQueueMo>) mdbTables.getList(renewalRateQueueMo.getField());
        if (CollectionUtils.isEmpty(renewalRateQueueMos)) {
            return null;
        }
        for(RenewalRateQueueMo mo:renewalRateQueueMos) {
            if(mo.getRatePlanId()==ratePlanId&&mo.getStartDate()<=eventTime&&eventTime<=mo.getEndDate())
                return mo;
        }
        return null;
    }
    /**
     * 根据IMSI获取 设备IMSI
     *
     * @param imsi
     * @return
     */
    public DeviceImsiMo getDeviceImsiMo(String imsi, Long startTime) {
        mtDeviceImsi.setImsi(imsi);
        mdbTables.selectTables(mtDeviceImsi);
        @SuppressWarnings("unchecked")
        List<DeviceImsiMo> deviceImsiMos = (List<DeviceImsiMo>) mdbTables.getList(deviceImsiMo.getField());
        if (CheckNull.isNull(deviceImsiMos) || deviceImsiMos.isEmpty()) {
            logger.error("##########getDeviceImsiMo()->deviceImsiMos is null.#############");
            return null;
        }

        long nowTime = Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS));
        logger.info("############nowTime:{}, cdr startTime:{}##################", nowTime, startTime);
        for (DeviceImsiMo deviceImsiMo: deviceImsiMos) {
            if (deviceImsiMo.getStartDate()>startTime || deviceImsiMo.getEndDate()<startTime) {
                logger.info("############DeviceImsiMo is dropped. imsi data is:{}##############", deviceImsiMo.toString());
                continue;
            }

            logger.info("############DeviceImsiMo is get. imsi data is:{}##############", deviceImsiMo.toString());
            return deviceImsiMo;
        }

        return null;
    }

    /**
     * 根据msisdn获取 设备msisdn
     *
     * @param msisdn
     * @return
     */
    public DeviceMsisdnMo getDeviceMsisdnMo(String msisdn, Long startTime) {
        mtDeviceMsisdn.setMsisdn(msisdn);
        mdbTables.selectTables(mtDeviceMsisdn);
        @SuppressWarnings("unchecked")
        List<DeviceMsisdnMo> deviceMsisdnMos = (List<DeviceMsisdnMo>) mdbTables.getList(deviceMsisdnMo.getField());
        if (CheckNull.isNull(deviceMsisdnMos)) {
            logger.error("##########getDeviceMsisdnMo()->deviceMsisdnMos is null.#############");
            return null;
        }

        long nowTime = Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS));
        logger.info("############nowTime:{}, cdr startTime:{}##################", nowTime, startTime);
        logger.info("############nowTime:{}##################", nowTime);
        for (DeviceMsisdnMo deviceMsisdnMo: deviceMsisdnMos) {
            if (deviceMsisdnMo.getStartDate()>nowTime || deviceMsisdnMo.getEndDate()<nowTime) {
                logger.info("############DeviceMsisdnMo is dropped. Msisdn data is:{}##############", deviceMsisdnMo.toString());
                continue;
            }

            logger.info("############DeviceMsisdnMo is get. Msisdn data is:{}##############", deviceMsisdnMo.toString());
            return deviceMsisdnMo;
        }
        return null;
    }

    /**
     * 获取mdb 账户详情
     *
     * @param acctId
     * @return
     */
    public AccountMo getMdbAccount(long acctId) {
        mtAccount.setAcctId(acctId);
        mdbTables.selectTables(mtAccount);
        return (AccountMo) mdbTables.getData(accountMo.getField());
    }
}
