package com.ai.iot.bill.common;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.cdr.Cdr;
import com.ai.iot.bill.common.cdr.CdrAttri;
import com.ai.iot.bill.common.cdr.CdrDisp;
import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.mq.ZkMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.ProtostuffUtil;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.ZkLock;

class MyThread extends Thread {
	@SuppressWarnings("unused")
	private int i = 0;
	private final static Logger logger = LoggerFactory.getLogger(MyThread.class);

	@Override
	public void run() {
		System.out.println("MyThread.run(1)"); 
		
		CustJedisCluster jc=RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_PARAM);
		
		System.out.println("MyThread.run(2)"); 

		String ret=jc.set("aaa".getBytes(), "2".getBytes());
		logger.info("------ret:"+ret);
	}
}

class protoTest implements Serializable{
	private static final long serialVersionUID = -5808931428425330529L;
    public int id;
	public String name;
	private int code;
	private String value;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

public class AppTest {
	private final static Logger logger = LoggerFactory.getLogger(AppTest.class);
	
    @SuppressWarnings("unused")
	private static AppTest appTest = new AppTest();
    
    @Before
    public void setUp() throws Exception {
        
    }
    
    @Test
    public void testMdbUtil1(){
		CustJedisCluster jc=RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_PARAM);
		/*byte[] ret1 = jc.get("a".getBytes());
		byte[] ret2 = jc.get("aa".getBytes());
		byte[] ret3 = jc.get("aaa".getBytes());*/
		/*if(ret1!=null)
			logger.debug("------ret1:"+(new String(ret1)));
		if(ret2!=null)
			logger.debug("------ret2:"+(new String(ret2)));
		if(ret3!=null)
			logger.debug("------ret3:"+(new String(ret3)));

		String ret=jc.set("aaa".getBytes(), "2".getBytes());
		logger.debug("------ret:"+ret);*/
		long max=System.currentTimeMillis() / 1000 - 10 - 60;
		Set<byte[]> fields = jc.zrangeByScore("201+2".getBytes(Const.UTF8), 0, max, 0, 100);
		System.out.println("------ret:"+fields.size());
	}
    
    @Test
    public void testProto(){
    	protoTest obj = new protoTest();
    	obj.setId(123);
    	obj.setCode(456);
    	obj.name = "hahaha";
    	obj.setValue("=====");
    	
    	byte[] b1=ProtostuffUtil.serializer(obj);
    	protoTest obj2 = ProtostuffUtil.deserializer(b1,protoTest.class);
    	
    	logger.debug("==>{}",obj2.name);
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testProtoList(){
    	protoTest obj = new protoTest();
    	obj.setId(123);
    	obj.setCode(456);
    	obj.name = "传统的用于完成计费功能的Radius协议，以其简单安全，易于管理，扩展性好，而得到广泛应用。但是由于协议本身的缺陷，比如基于UDP的传输、简单的丢包机制、没有关于重传的规定和集中式计费服务，都使得它不太适应当前网络的发展，需要进一步改进。"
+"随着新的接入技术的引入和移动网络的快速扩容，对AAA协议提出了新的要求，使得传统的RADIUS结构的缺点日益明显。目前3G网络正逐步向全IP网络演进，不仅在核心网络使用支持IP的网络实体，在接入网络也使用基于IP的技术，而且移动终端也成为可激活的IP客户端。这就需要采用新一代的AAA协议——Diameter。Diameter基础协议为各种认证、授权和计费业务提供了安全、可靠、易于扩展的框架。以此为基础定义Diameter应用，只需要定义应用协议的应用标识、参与通信的网络功能实体、相互通信的功能实体间的消息内容以及协议过程，就可以完全依赖Diameter基础协议完成特定的接入和应用业务。Diameter协议具有如下特性：" 
+"(1)拥有良好的失败机制，支持失败替代(failover)和失败回溯(faiback)；"
+"(2)拥有快速检测到对端不可达的能力； "
+"(3)拥有更好的包丢弃处理机制，Diameter协议要求对每个消息进行确认；"
+"(4)可以保证数据体的完整性和机密性； "
+"(5)支持端到端安全，支持TLS和IPSec； "
+"(6)为每个会话进行认证/授权，以保证安全性； "
+"在Diameter基础协议上扩展的应用协议Diameter Credit Control Application，定义了针对预付费用户的计费机制，采用信用额度控制实现了基于会话及事件的计费，解决了对于预付费的计费需求。";
    	obj.setValue("=====");
    	
    	List<protoTest> objlist = new ArrayList<>();
    	objlist.add(obj);
    	byte[] b1=ProtostuffUtil.serializerList(objlist);
    	List<protoTest> objlist2 = ProtostuffUtil.deserializerList(b1);
    	//List<protoTest> objlist2 = c2.getList();
    	protoTest obj2 = objlist2.get(0);
    	
    	logger.debug("==>listsize={},size={},namesize={},name={}",objlist2.size(),b1.length,obj2.name.getBytes().length,obj2.name);
    }
    
    /*@SuppressWarnings("unchecked")
	@Test
    public void testMdbTables(){
    	CustJedisCluster jc=RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_PARAM);
    	MdbTablesRating dev = new MdbTablesRating(jc);
    	boolean ret=dev.getRatingData(MdbTablesRating.TABLE_TYPE_DEVICE_DATA, "201706","88888888","GUID1234");
    	
    	List<protoTest> objlist = (List<protoTest>) dev.getList("SUM-PRE");
    	logger.debug("==> objlist={}",objlist);
    	Object guid=dev.getData("GUID1234");
    	logger.debug("==> guid={}",guid);
    	
    	protoTest obj = new protoTest();
    	obj.setId(123);
    	obj.setCode(456);
    	obj.name = "传统的用于完成计费功能";
    	obj.setValue("==12345===");
    	objlist = new ArrayList<>();
    	objlist.add(obj);
    	
    	dev.setData("SUM-PRE", objlist);
    	dev.setData("GUID1234", System.currentTimeMillis());
    	int iret=dev.updateTablesWithVer();
    	logger.debug("==> iret={}",iret);
    }*/
    
    @Test
    public void testKafkaSend(){
    	KafkaMq mq=KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_INFO, Const.READ_AND_WRITE);
    	//mq.registerClass(protoTest.class);
    	String topic = "TOP_INFO_DEV_INFO";
    	if(mq!=null){
    		protoTest obj = new protoTest();
        	obj.setId(123);
        	obj.setCode(456);
        	obj.name = "传统的用于完成计费功能";
        	obj.setValue("==12345===");
        	
    		int p = mq.sendMsgObject(topic, obj);
    		logger.info("===>send:{}",p);
    	}    	
    }
    
    @Test
    public void testKafkaRead(){
    	KafkaMq mq=KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_INFO, Const.READ_AND_WRITE);
    	//mq.registerClass(protoTest.class);
    	String topic = "TOP_INFO_DEV_INFO";
    	if(mq!=null){
    		long now=System.nanoTime();
    		mq.setTopic(topic);
    		List<Object> ret=mq.recvMsgObjects(3000);
    		mq.commit();
    		Object obj3=ret.get(0);
    		logger.info("type={}",obj3.getClass().getName());
    		protoTest obj2=(protoTest) obj3;
    		logger.info("ret={},now={},run time={} ms",obj2.name,obj3.getClass().getName(),(System.nanoTime()-now)/1000000);
    	}    	
    }

    @Test
    public void testMyThread() {
    	MyThread th = new MyThread();
    	th.start();
    	try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testZkUtil(){
    	ZkLock zkLock = ZkMgr.getPartitionLock(BaseDefine.CONNTYPE_ZK_INFO, "TOP_CDRS_TRANS4",2);
    	logger.info("===>zkLock:{}",zkLock.getPartition());
    	
    	assertTrue(zkLock.getPartition()>=0);
    }
    
    @Test
    public void testAttri(){
    	Cdr cdr=new Cdr();
    	cdr.set(CdrAttri.ATTRI_GUID, "guid-1234");
    	cdr.set(CdrAttri.ATTRI_SOURCE_TYPE, "1");
    	cdr.set(CdrAttri.ATTRI_SP_CODE, "sp000");
    	cdr.set(77, "no define");
    	cdr.set(CdrAttri.ATTRI_RG_ID, "rg0");
    	cdr.set(CdrAttri.ATTRI_DATA_TOTAL, "1024");
    	cdr.set(CdrAttri.ATTRI_RG_ID,1, "rg1");
    	cdr.set(CdrAttri.ATTRI_DATA_TOTAL,1, "2048");
    	cdr.set(CdrAttri.ATTRI_RG_ID,2, "rg2");
    	cdr.set(CdrAttri.ATTRI_DATA_TOTAL,2, "3096");
    	cdr.set(CdrAttri.ATTRI_BIZ_TYPE, "31");
    	
    	CdrDisp.disp(cdr);
    }
    
    /**
     * 测试mysql连接超时时长为8小时，超过则断开，测试连接池如何保证获取到的连接可用
     */
    @Test
    public void testJdbcTimeout(){
    	DataSource ds = new DataSource();
    	PoolProperties p = new PoolProperties();
    	
    	p.setDriverClassName("com.mysql.jdbc.Driver");
    	p.setUrl("jdbc:mysql://127.0.0.1:3306/param?useUnicode=true&characterEncoding=UTF8&useSSL=false");
    	p.setUsername("param");
    	p.setPassword("param123");
    	p.setInitialSize(1);
    	p.setMaxActive(20);
    	p.setMinIdle(1);
    	p.setMaxIdle(10);
    	p.setMaxAge(8000);
    	p.setMinEvictableIdleTimeMillis(25200000);
    	p.setTimeBetweenEvictionRunsMillis(60000);
    	
    	p.setMaxWait(200000);
    	
    	ds.setPoolProperties(p);
    	
    	try {
    		String sqlStr="select count(1) from td_b_param";
    		Connection conn = ds.getConnection();
    		Statement stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(sqlStr);
            logger.info("1 {} return {}",conn.getNetworkTimeout(),rs.next());
            conn.close();
            Thread.sleep(10000);
            conn = ds.getConnection();
    		stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlStr);
            logger.info("2 {} return {}",conn.getNetworkTimeout(),rs.next());
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
