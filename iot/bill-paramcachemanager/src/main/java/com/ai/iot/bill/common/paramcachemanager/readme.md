#参数缓存管理

##概述
参数缓存管理负责从MDB-param的参数内存库中获取参数信息,并加载到本地私有内存的缓存中,为进程或线程的业务处理提供快速的参数访问机制,主要包括:参数加载、缓存管理线程、客户端API

- 参数加载
    - 负责将物理库的参数加载到内存库MDB
- 缓存管理线程
    - 负责MDB和本地缓存的同步,保证本地缓存数据及时更新
    - 更新采用全量覆盖方式
    - 通过AB两块内存进行切换处理,客户端始终访问最新的版本数据,同步加载时使用另外一块内存进行更新,前提是检测到客户端没有访问另外一块内存,更新结束后切换到最新的缓存版本数据,同时删除旧版本数据.客户端再次访问时使用最新版本的缓存数据进行查询
    - 加载成功或切换成功时需汇报本地缓存信息和客户端访问者信息到MDB
    - 定期将客户端访问者信息汇报到MDB
- 客户端API
    - 每个业务线程可以获取一个客户端对象
    - 客户端注册时会将客户端信息汇报到MDB,注销时会从MDB里边删除

##目录结构
paramcachemanager包含下面的子目录:

- core/loader:参数加载模块,负责从物理库加载到内存库MDB
- core/manager:参数缓存管理线程和客户端API的实现
- core/mdb:mdb连接对象和操作的封装
- core/pobase:基本的数据实体定义
- pog:所有po组的注册
- pog/xxxx:各个业务模块使用的po组的定义和注册.不建议同一个表在多个pog进行复用加载,会引起重复加载,多占用内存.
    
##参数加载

为单独一个模块,运行在jstorm上面.

加载运行步骤(前两种为测试方法):

1. 修改物理表td_update_group_flag中的POG对应的UPDATE_FLAG字段为一个新的数值，随便改成与之前不一样的时间。

2. 方法:
    - （方法一）common 工程下的 test 用例位置：
   
            com.ai.iot.bill.common.paramcachemanager.ParamTest
            运行该单元测试方法:main() 完成后，即实现了一次加载。
            非Topology封装，速度快，开发期间推荐！

    - （方法二）bill-param 工程下的 test 用例位置
          
            com.ai.iot.bill.ParamLoaderTest
            运行该单元测试方法:main() 完成后，即实现了一次加载。
            Topology封装后的本地测试用例。开发期间不推荐。

    - （方法三）bill-param 工程打包成jar 
    
            自己搭建环境，提交topology，运行测试。

 3、加载完以后，查看对应的redis集群PARAM组中 MDB中有PO POG的相关信息是否已经加载进去


##参数管理组件的使用(按需加载模块需要的Po组)

    ///////////////////////////////////////////////////    
    //进程级操作    
    ///////////////////////////////////////////////////
    //初始化参数管理组件，并获取客户端对象
	ParamCacheManagerConfigure.getGlobalInstance().init();
    //代码级个性化配置项
	ParamCacheConfigure configure = ParamCacheManagerConfigure.getGlobalInstance();
	configure.setSerializeType(AppTest.serializeType);
    //设置本模块需要加载的Po组,多个Po组可以调用多次,可以减少内存占用
	configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName());
    //获取管理对象
	manager = ParamCacheManager.getGlobalInstance();
	//初始化管理对象,仅需调用一次,不同的线程可以多次调用,不过建议仅调用一次
	manager.initialize(configure);
    
    ///////////////////////////////////////////////////    
    //线程级操作    
    ///////////////////////////////////////////////////
    //获取客户端,可以多次获取,一个业务线程一个,不允许一个线程多个客户端对象
	client = manager.getInstanceClient();
    //客户端注册
	client.register();
    //客户端空闲时的心跳调用
    client.heartBeat();
    
    ///////////////////////////////////////////////////    
    //事件级操作    
    ///////////////////////////////////////////////////
    //开始操作事件之前
    client.beginTrans();
    //PO数据的遍历
    ps=new PoSample();
	List<PoBase> dataList = client.getAllDatas(ps.getPoGroupName(),ps.getPoName());
    if (CheckNull.isNull(dataList)) {
	//...
	}
	for (PoBase data : dataList) {
	//...
	}
    //PO数据的查询
    ps=new PoSample();
	ps.setTabGroupName("POG_SAMPLE");
	dataList = client.getDatasByKey(ps.getPoGroupName(),ps.getPoName(),PoSample.getIndex1Name(),ps.getIndex1Key());
	if (CheckNull.isNull(dataList)) {
	//...
	}
	for (PoBase data : dataList) {
	//...
	}
    //结束事件操作之后
    client.endTrans();

    ///////////////////////////////////////////////////    
    //线程级操作    
    ///////////////////////////////////////////////////
    //业务线程退出
	manager.removeClient(client.getClientId());

    ///////////////////////////////////////////////////    
    //进程级操作    
    ///////////////////////////////////////////////////
    //进程退出时
    manager.finalize();

##使用Pm查询器可以简化编码操作,并且尽可能避免频繁申请对象而引起gc

注:pm类统一存放在每个Po组下面,每个组一个pm类

    ///初始化时保存Pm对象
    //...
    PmBase pmSample=new PmSample(client);
    
    ///数据遍历
    List<PoBase> dataList = pmSample.getPoSampleAllDatas();
    if (CheckNull.isNull(dataList)) {
	//...
	}
	for (PoBase data : dataList) {
	//...
	}
    ///根据key1查询
    String tabGroupName="POG_SAMPLE";
    dataList = pmSample.getPoSampleDatasByKey1(tabGroupName);
    if (CheckNull.isNull(dataList)) {
	//...
	}
	for (PoBase data : dataList) {
	//...
	}
    ///根据key2查询
    long version=20170829120322L;
    dataList = pmSample.getPoSampleDatasByKey2(version);
    if (CheckNull.isNull(dataList)) {
	//...
	}
	for (PoBase data : dataList) {
	//...
	}
    ///根据key3查询
    dataList = pmSample.getPoSampleDatasByKey3(tabGroupName,version);
    if (CheckNull.isNull(dataList)) {
	//...
	}
	for (PoBase data : dataList) {
	//...
	}

##增加参数Po组或Po类
步骤:

1. 在pog下增加对应的目录xxx
2. 在pog/xxx下面新增需要的Po类,需从PoBase继承,并实现Serializable接口,需实现至少一个索引,否则不会加载.为管理方便,可以酌情增加子目录
4. 在pog/xxx下面新增PoGroupRegisterXxx类,从PoGroupRegister继承,并注册对应的Po类
3. 修改pog下的PoGroupRegisterFactory,添加新增的Po组注册类PoGroupRegisterXxx
5. 发布最新的jar包,重启相关使用的模块进程

命名规则:

- 目录名都小写
- pog组名都大写

**==>【增加参数Po组\Po类\Pm类】的代码示例**

此处以话单格式Po类为例，话单格式Po类为PoCdrFormat，隶属于系统PO组。

    ///////////////////////////////////////////////////    
    //从PoBase继承,并实现Serializable接口   
    ///////////////////////////////////////////////////
    public class PoCdrFormat extends PoBase implements Serializable {
    private static final long serialVersionUID = 6549759358225405557L;
	//模块名称
    private String procName;
    //模块输入输出标识
    private int inOutFlag;
    //数据源类型
    private int sourceType;
    //格式ID
    private int formatId;
    //格式描述
    private String formatRemark;

    ///////////////////////////////////////////////////    
    //参数Obj 是一个本PO对应的物理表的一条记录的所有字段（column）
    //的 List，需要把每个字段对应到本PO的各个属性上去。  
    ///////////////////////////////////////////////////
    @Override
    public boolean fillData(Object obj) {
        List<String> fields = (List<String>)obj;
        procName = fields.get(0);
        inOutFlag = StringUtil.toInt(fields.get(1));
        sourceType = StringUtil.toInt(fields.get(2));
        formatId = StringUtil.toInt(fields.get(3));
        formatRemark = fields.get(4);
        return false;
    }

    ///////////////////////////////////////////////////    
    //返回本PO所属PO组的名称 
    ///////////////////////////////////////////////////
    @Override
    public String getPoGroupName() {
        return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.toString();
    }

    ///////////////////////////////////////////////////    
    //创建本PO所拥有的索引
    //以第一条POCdrFormatBaseIndex1为例，本PO被加载到MDB后，
    //MDB中就会多了一条记录：key: 304+PO组名+PO名+更新时间戳+'INDEX' value:POCdrFormatBaseIndex1
    //实际为：key:304+POG_SYS+PoCdrFormat+20170725xxxxxx+INDEX  value:POCdrFormatBaseIndex1
    //getIndex1Key 为必须要实现的索引方法名
    ///////////////////////////////////////////////////
    @Override
    public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
        Map<String,Method> indexMethods=new HashMap<String,Method>();
        indexMethods.put("POCdrFormatBaseIndex1",PoCdrFormat.class.getMethod("getIndex1Key"));
        indexMethods.put("POCdrFormatBaseIndex2",PoCdrFormat.class.getMethod("getIndex2Key"));
        indexMethods.put("POCdrFormatBaseIndex3",PoCdrFormat.class.getMethod("getIndex3Key"));
        return indexMethods;
    }

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public int getInOutFlag() {
        return inOutFlag;
    }

    public void setInOutFlag(int inOutFlag) {
        this.inOutFlag = inOutFlag;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public String getFormatRemark() {
        return formatRemark;
    }

    public void setFormatRemark(String formatRemark) {
        this.formatRemark = formatRemark;
    }

    ///////////////////////////////////////////////////    
    //至少要实现1个索引
    ///////////////////////////////////////////////////
    /** 具体的索引对应的key组成*/
    public String getIndex1Key() {
        return this.procName;
    }
    public String getIndex2Key() {
        return String.valueOf(this.formatId);
    }
    public String getIndex3Key() {
        return String.valueOf(this.sourceType);
    }
    }

    /**
     * 系统PO组的注册器，本PO组包含哪些PO 需要在这里注册
     *
     */
    public class PoGroupRegisterSys extends PoGroupRegister {
    
        @Override
        public String getPoGroupName() {
            return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.getName();
        }

    ///////////////////////////////////////////////////    
    //把属于本PO组的PO注册到PO组，此处PoCdrFormat、PoCdrField、
    //PoFilterScript、PoFilterScriptType都是POG_SYS这个PO组下的Po.
    ///////////////////////////////////////////////////
        @Override
        public void setAllPoObjects() {
            PoBase cdrFormat = new PoCdrFormat();
            PoBase cdrField = new PoCdrField();
            PoBase filterScript = new PoFilterScript();
            PoBase filterScriptType = new PoFilterScriptType();
            allPoObjects.put(cdrFormat.getPoName(), cdrFormat);
            allPoObjects.put(cdrField.getPoName(), cdrField);
            allPoObjects.put(filterScript.getPoName(), filterScript);
            allPoObjects.put(filterScriptType.getPoName(), filterScriptType);
        }
    }
    /**
     * 系统PO组的查询器，本PO组的方法查询在此定义
     *
     */
    public class PmSys extends PmBase {
	///为减少内存的频繁申请引起gc,每个po在此产生一个永久的对象,用于临时数据传递.
	PoCdrFormat cdrFormat=new PoCdrFormat();
	
	public PmSys(ParamClient paramClient) {
		super(paramClient);
	}
	
	/**返回指定索引1的key对应的所有数据
	 * @param key key数据,来自Po定义
	 * */
	public List<PoBase>  getcdrFormatDatasByKey1(String cdrField1) {
        cdrFormat.setCdrField1(cdrField1);
		return paramClient.getDatasByKey(cdrFormat.getPoGroupName(), 
                                            cdrFormat.getPoName(),
											PoCdrFormat.getIndex1Name(),
                                            cdrFormat.getIndex1Key()) ;
	}

##配置

- paramcachemanager.properties
    - jar包里边默认带有一个paramcachemanager.properties配置文件
    - 支持在系统环境里边配置IOT_HOME的时候会以$IOT_HOME/conf/paramcachemanager.properties覆盖默认配置
    - 覆盖次序:

            $IOT_HOME/conf/paramcachemanager.properties -> 
            resource/paramcachemanager.properties ->
            default(代码写死)

- po_sql.properties
 
    在common 模块的资源目录下的po_sql.properties中添加该PO对应的查询语句.

    例：

        PoCdrFormat = SELECT PROC_NAME AS procName, INOUT_FLAG AS inoutFlag, 
        SOURCE_TYPE AS sourceType, FORMAT_ID as formatId, FORMAT_REMARK as 
        formatRemark FROM td_b_cdr_format;

- ehcache3-paramcachemanager.xml

    ehcache3版本对应的缓存配置文件,里边仅配置模版,代码里边会每个Po组产生一个缓存对象

- ehcache-paramcachemanager.xml
    
    ehcache2版本对应的缓存配置文件,里边仅配置模版cache,代码里边会以此每个Po组产生一个缓存对象

- ehcache.xsd
    
    ehcache2版本对应的xsd文件,运行非必须.

- 配置要求:

    - 因为通过反射获取配置信息,所以需保持key名称和代码的一致,否则无法覆盖代码的默认值
    - 一般配置'主'方式连接mdb
    - 如果需要密码,请配置加密后的密码串,使用Base64编码
    - mdb尽量配置多个主机和端口,提供高可用
    - 默认使用ehcache,可以通过配置改成私有内存的hashmap
    - 序列化默认使用FST,可以加快同步速度,同时减少mdb内存占用,可以修改成fastjson,方便查看,但是会降低同步速度,增加一点mdb内存占用.
    - 如果mdb主机端口不配置,则从物理库的路由表中获取连接信息,否则就不依赖物理库.


##其他说明

- mdb-param一般仅部署'主'mdb,无需'从'mdb,同时打开持久化,增加可靠性.
- 尽量配置mdb的主机和端口,减少模块对物理库的依赖.



