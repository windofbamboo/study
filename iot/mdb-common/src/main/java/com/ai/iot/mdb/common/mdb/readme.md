【资料管理】之PDB到MDB 编码流程说明

1、注册Mo组。 代码位置：MoBaseRegisterFactory类中。

    public static void initAllMoGroup() {
        MoGroup moGroup = new DeviceInfoMoGroup();
        moGroupMap.put(moGroup.getName(), moGroup);

        //增加PO组需要在此新增注册
        //moGroup = new AutoRuleMoGroup();
        //moGroupMap.put(moGroup.getName(), moGroup);

        return;
    }
    
2、添加Mo。  代码位置：com.ai.iot.mdb.common.mdb.mo。

   请参照：com.ai.iot.mdb.common.device.DeviceMo 的具体代码实现及注释。
   
3、运行com.ai.iot.bill.infoload.InfoloadTest的测试用例。
  （测试用例文件位置：git仓库:IOT-BILL\src_j\bill-infoload\other-data\InfoloadTest.java）
   运行完毕后，即可查看MDB中加载进来的数据。
   为防止交叉使用测试用例，建议复制一份测试用例自行测试。
   测试用例中可以配置使用自己本地的REDIS集群环境。
   conf.put("redis.routeid", BaseDefine.CONNTYPE_REDIS_XXXXXX);

【相关概念】：
（1）相关Mo。
从物理库中加载一个Mo时，不得已连带的也需要一起加载其他的Mo，则这些其他的Mo成为本Mo的相关Mo。
举例说明：
设备MO：DeviceMo
设备Mo相关Mo：RatePlanMo、RatePlanMo、DeviceMsisdnMo、DeviceImsiMo
Mo              key               field                 disp_type
////////////////////////////////////////////////////////////////////////////////////
DeviceMo-      【key:DeviceId】【field:DEVICE】       【分发类型（DispType）:DeviceId】
////////////////////////////////////////////////////////////////////////////////////
RatePlanMo-    【key:DeviceId】【field:RATE_PLAN】    【分发类型（DispType）:DeviceId】
DeviceMsisdnMo-【key:Msisdn】  【field:DEVICE】       【分发类型（DispType）:DeviceId】
DeviceImsiMo-  【key:Imsi】    【field:DEVICE】       【分发类型（DispType）:DeviceId】

当一条 DeviceMo 被加载时，可能本条DeviceMo 会对应多条RatePlanMo，也需要加载到MDB中，
那么RatePlanMo就是 DeviceMo 的一个相关Mo。
同理DeviceMsisdnMo、DeviceImsiMo 也是DeviceMo 的相关Mo。

对于一个Mo，如需加载相关Mo时，需要在参数表(td_b_param)中配置连带关系以DeviceMo为例：
    param_type              param_name               param_value             param_value2
    ////////////////////////////////////////////////////////////////////////////////////
    MO_RELATION_DEVINFO      2                       DeviceMo               DeviceRatePlanMo
    MO_RELATION_DEVINFO      3                       DeviceMo               DeviceMsisdnMo
    MO_RELATION_DEVINFO      4                       DeviceMo               DeviceImsiMo
    
如有其他问题，请直接联系本人。

written by zhangrui 20170810

DeviceMo 编码示例：

    public class DeviceMo extends MoBase {
    private final static Logger logger = LoggerFactory.getLogger(DeviceMo.class);

    private Long deviceId;
    private String iccid;
    private Long acctId;
    private int provinceCode;
    private String msisdn;
    private String imsi;
    private boolean simBarred;
    private int overageLimitOverwrite;
    private int status;
    private Long updateTime;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public boolean isSimBarred() {
        return simBarred;
    }

    public void setSimBarred(boolean simBarred) {
        this.simBarred = simBarred;
    }

    public int getOverageLimitOverwrite() {
        return overageLimitOverwrite;
    }

    public void setOverageLimitOverwrite(int overageLimitOverwrite) {
        this.overageLimitOverwrite = overageLimitOverwrite;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public byte[] getKey() {
        StringBuffer key = new StringBuffer();
        key.append(MoBaseRegisterFactory.mdbOperaCode_DEVICE_ID);
        key.append(MoBaseRegisterFactory.separator);
        key.append(deviceId);
        return key.toString().getBytes();
    }

    @Override
    public byte[] getField() {
        return MoBaseRegisterFactory.hashField_DEVICE.getBytes();
    }

    //获取本MO的总体信息：物理表中的ID总数，最小ID，最大ID。
    @Override
    public TotalInfo getTotalInfo(Connection conn, QueryRunner qr) {
        TotalInfo totalInfo = null;
        try {
            totalInfo = BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getDeviceMoTotalInfo");
        } catch (Exception e) {
            logger.error("getPlatSmsValue err ", e);
        }
        return totalInfo;
    }

    //把所有的ID划分成多个片，自己定义ID分片的方法，对于DeviceMo,默认采用AccountMo的分片方法：
    //例如一下5条ID，将会被分成5片：3000000000000000~300099999999999、3100000000000000~310099999999999、
    //3200000000000000~320099999999999、3300000000000000~330099999999999、3400000000000000~340099999999999
    //示例ID：
    //3000000001000111
    //3000000002000111
    //3000000002000112
    //3000000003000112
    //3400000003000111
    @Override
    public List<SegInfo> sliceIdsIntoSegs(TotalInfo totalInfo) {
        if (CheckNull.isNull(totalInfo)) {
            return null;
        }

        return AccountMo.defaultSliceIds(totalInfo,100, 1000000000000L);
    }

    //分片去捞所有ID。
    //资料加载的时候根据ID，一片一片的取，为了达到边取边分发加载的效果。
    @Override
    public List<KeyId> fetchBatchIds(Connection conn, QueryRunner qr, SegInfo segInfo) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getDeviceIdSeg", segInfo.getMinId(), segInfo.getMaxId());
    }

    //派发的ID是不是本MO的key中的ID。
    //如加载DeviceInfo时，是按deviceId 派发的，那么就return true;
    //而一条设备记录相关的DeviceImsi之类的Mo, 因为派发时的DeviceId 并不是DeviceImsiMo的Key中的id，此时就需要返回false.
    @Override
    public boolean isKeyId() {
        return true;
    }

    //返回本MO对应的派发类型
    @Override
    public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
        return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_DEVICE;
    }

    //本MO从物理库加载的具体实现。单条
    @Override
    public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getMdbDeviceById", id);
    }

    //本MO从物理库加载的具体实现。多条
    @Override
    public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
        return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getMdbDeviceById", id);
    }
    }
