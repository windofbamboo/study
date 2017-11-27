package com.ai.iot.bill.common.util;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.BillException.BillExceptionENUM;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name},此类多线程安全]
 * @Description: [ 分布式序列要求： 1）同一个ID序列的产生支持多key并发，减少单实例压力 2）多key需考虑分段需求
 *               3）使用redis集群，并且方便扩展 实现机制：
 *               key=SEQ_<name>_<m>,value=12位yymmddhhmiss+6位value（前补0），每次加m,返回的ID=value
 *               配置项： 序列名name（可选，默认为DEFAULT）、 序列总长度L(value长度=L-12,最小12，默认为16位)、
 *               当前序列并发key的数目m（可选，默认为3，m整数的长度每超过1位，建议L加1，保证value始终有5位用于自增，减少重复概率）、
 *               客户端定位key的方式（可选，默认为轮询）、 脚本资源方式（可选，默认为资源文件）、
 *               脚本资源文件or脚本内容（可选，默认资源文件为DistributedUniqueID.lua）
 * 
 *               优点：固定长度，最小长度为12位，扩展或异常不会重复，采用集群
 *               缺点：ID未体现时间信息，ID的时间仅仅是在第一次初始化ID的时间 注意： 1）客户端调用incrby获取ID的值
 *               2）客户端发现ID长度<L时重新初始化value，重新初始化的功能调用服务端的lua实现，lua内容可以再次简化
 *               3）客户端的<m>值根据随机数和m取模动态计算获取，或者使用轮询机制获取<m>,最后计算最终的key
 *               4）返回接口支持string和long两种类型，方便将来扩展 配置方式： 
 *               seqlist=duidName1,duidName2....
 *               <seq>.keypref=DSEQ_ 
 *               <seq>.name=TF_F_ACCT_ACCT_ID
 *               <seq>.length=18 
 *               <seq>.keynum=3 
 *               <seq>.keymode=0（轮询），1（随机）
 *               <seq>.valuemode=0（仅一次初始化），1（按天初始化,天只增不减，在主机时间和mdb均异常的情况下有一定的重复风险） 
 *               <seq>.luamode=0（资源文件名），1（内容）
 *               <seq>.luaresource=DistributedUniqueID.lua
 *               <seq>.redisconntype=33
 *               <seq>.timezone=8
 *               如果时间格式转换没问题的话，可以每天初始化一次初始值， 在客户端记录上一次的ID，获取新ID值后做跨天判断，
 *               发到服务端重置（lua内需再做跨天判断，避免多次重置，时钟同步可能会引起ID重复），
 *               这样id就带准确的yymmdd信息了，但是如果服务端时间重置错误时会有些问题（内部保证日期只会变大，不会变小） ]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * 
 */
public class DistributedUniqueIdFactory {
    private static final Logger logger = LoggerFactory.getLogger(DistributedUniqueIdFactory.class);
    /// 配置文件
    private static final String DISTRIBUTED_UNIQUE_ID_CONF = "distributed.unique.id.properties";
    
    /// duidName和配置的映射
    private static Map<String,DistributedUniqueIdConf> duidConfMap=new HashMap<>();
    /// duidName和对象的映射
    private static Map<String,DistributedUniqueId> duidObjMap=new HashMap<>();
    
    public static class DistributedUniqueIdConf {
        // 序列key的前缀
        public static final String SEQ_LIST_NAME = "seqlist";
        public static final String SEQ_KEY_PREFIX = "DSEQ_";
        public static final String SEQ_KEY_NAME = "DEFAULT";
        public static final int SEQ_KEY_LENGTH = 16;
        public static final int SEQ_KEY_LENGTH_MIN = 12;
        public static final int SEQ_KEY_NUM = 3;
        public enum KEY_MODE{
            KEY_NEXT,KEY_RAND
        }
        public enum VALUE_MODE{
            VALUE_ONCE,VALUE_DAILY
        }
        public enum LUA_MODE{
            LUA_RESOURCE_FILE,LUA_RESOURCE_CONTENT
        }
        /// 初始化生成序列的lua脚本
        public static final String SEQ_LUA_RESOURCE = "distributed.unique.id.lua";
        public static final int SEQ_TIME_ZONE_DEFAULT = 8;
        public static final int SEQ_IDS_CACHE_NUM = 1;
        
        private String keyPrefix=SEQ_KEY_PREFIX;
        private String keyName=SEQ_KEY_NAME;
        private int keyLength=SEQ_KEY_LENGTH;
        private int keyNum=SEQ_KEY_NUM;
        private int idsCacheNum=SEQ_IDS_CACHE_NUM;
        private KEY_MODE keyMode=KEY_MODE.KEY_NEXT;
        private VALUE_MODE valueMode=VALUE_MODE.VALUE_ONCE;
        private LUA_MODE luaMode=LUA_MODE.LUA_RESOURCE_FILE;
        private String luaResource=SEQ_LUA_RESOURCE;
        private int redisConnType=BaseDefine.CONNTYPE_REDIS_PARAM;
        private int timeZone=SEQ_TIME_ZONE_DEFAULT;
        
        public void setConf(Properties properties,String duidName) {
            String value;
            String key;
            
            key=duidName+".keypref";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.keyPrefix=value;
            }
            
            key=duidName+".name";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.keyName=value;
            }
            
            key=duidName+".length";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.keyLength=Integer.parseInt(value);
            }
            
            key=duidName+".keynum";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.keyNum=Integer.parseInt(value);
            }
            
            key=duidName+".idscache";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.idsCacheNum=Integer.parseInt(value);
            }
            
            key=duidName+".keymode";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.keyMode=KEY_MODE.values()[Integer.parseInt(value)];
            }
            
            key=duidName+".valuemode";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.valueMode=VALUE_MODE.values()[Integer.parseInt(value)];
            }
            
            key=duidName+".luamode";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.luaMode=LUA_MODE.values()[Integer.parseInt(value)];
            }
            
            key=duidName+".luaresource";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.luaResource=value;
            }
            
            key=duidName+".redisconntype";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.redisConnType=Integer.parseInt(value);
            }

            key=duidName+".timezone";
            value=properties.getProperty(key);
            if(value!=null) {
                value=value.trim();
                this.timeZone=Integer.parseInt(value);
            }
        }
        public String getKeyPrefix() {
            return keyPrefix;
        }
        public String getKeyName() {
            return keyName;
        }
        public int getKeyLength() {
            return keyLength;
        }
        public int getKeyNum() {
            return keyNum;
        }
        public int getIdsCacheNum() {
            return idsCacheNum;
        }
        public KEY_MODE getKeyMode() {
            return keyMode;
        }
        public VALUE_MODE getValueMode() {
            return valueMode;
        }
        public LUA_MODE getLuaMode() {
            return luaMode;
        }
        public String getLuaResource() {
            return luaResource;
        }
        public int getRedisConnType() {
            return redisConnType;
        }
        public int getTimeZone() {
            return timeZone;
        }
        public String getKey(int p) {
            return keyPrefix+keyName+"_"+p;
        }
        public String toString() {
            return JSONUtil.toJson(this);
          }
    }
    public static class DistributedUniqueId{
        private CustJedisCluster jedisCluster;
        private DistributedUniqueIdConf duic;
        private String lua;
        private byte[] curLuaSha;
        ///内容是map<curKey,luaSha>
        private Map<String,byte[]> luaShaMap=new HashMap<>();
        private Random rand;
        private int pos=0;
        private int trueKeyNumToServer=0;
        private String curKey;
        ///用于存放缓存的ID
        private Queue<String> idsCacheQueue=new LinkedList<>();

        ///任务
        private MyCallable buildRetryTaskCall=new MyCallable();
        ///执行次数限制的重试策略
        Retryer<String> retryer;
        private static class MyCallable implements Callable<String> {
            DistributedUniqueId obj;
            public void setProperties(DistributedUniqueId obj) {
                this.obj=obj;
            }
            @Override
            public String call() {
                return obj.innerNext(obj.curKey);
            }
        };
        public DistributedUniqueId(DistributedUniqueIdConf duic) throws BillException {
            this.duic=duic;
            rand=new Random();
            jedisCluster = RedisMgr.getJedisCluster(duic.getRedisConnType());
            if (jedisCluster == null) {
                throw new BillException(BillExceptionENUM.JDIS_CLUSTER_IS_NULL, "connType=" + duic.getRedisConnType());
            }
            if(duic.getKeyLength() <= DistributedUniqueIdConf.SEQ_KEY_LENGTH_MIN) {
                throw new BillException(BillExceptionENUM.CONFIG_IS_ERROR, "KeyLength=" + duic.getKeyLength());
            }
            if(duic.getKeyNum() <= 0) {
                throw new BillException(BillExceptionENUM.CONFIG_IS_ERROR, "KeyNum=" + duic.getKeyNum());
            }
            switch (duic.getLuaMode()) {
            case LUA_RESOURCE_FILE:
                lua=getScriptContent(duic.getLuaResource());
                break;
            case LUA_RESOURCE_CONTENT:
                lua=duic.getLuaResource()+"\r\n";
                break;
            default:
                throw new BillException(BillExceptionENUM.CONFIG_IS_ERROR, "luamode=" + duic.getLuaMode().ordinal());
            }
            buildRetryTaskCall.setProperties(this);
            curKey=duic.getKey(getKeyPos());
            trueKeyNumToServer=duic.getKeyNum();
            if(duic.getIdsCacheNum()>1) {
                trueKeyNumToServer=duic.getKeyNum()*duic.getIdsCacheNum();
            }
            luaShaMap.put(curKey,  jedisCluster.scriptLoad(lua.getBytes(Const.UTF8), curKey.getBytes(Const.UTF8)));
            retryer=RetryerBuilder.<String>newBuilder()
                    //如果返回结果为 null 则重试
                    .retryIfResult(Predicates.isNull())
                    //如果抛出 Exception 则重试
                    .retryIfExceptionOfType(Exception.class)
                    //最多尝试 3 次
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    //固定等待时长为 300 ms(返回下次执行的时间)
                    .withWaitStrategy(WaitStrategies.fixedWait(300, TimeUnit.MILLISECONDS))
                    .build();
        }
        private int getKeyPos() {
            return getKeyPos(duic.getKeyMode());
        }
        private int getKeyPos(DistributedUniqueIdConf.KEY_MODE km) {
            switch (km) {
            case KEY_NEXT:
                pos++;
                pos=pos%duic.getKeyNum();
                break;
            case KEY_RAND:
                pos=rand.nextInt(Integer.MAX_VALUE)%duic.getKeyNum();
                break;
            default://默认轮询
                pos++;
                pos=pos%duic.getKeyNum();
                break;
            }
            return pos;
        }
        
        ///删除服务器上面当前序列所有key对应的lua脚本
        ///一般情况下无需调用
        public void clearScripts() {
            if(jedisCluster==null||duic==null) {
                return;
            }
            for(int i=0;i<duic.getKeyNum();i++) {
                jedisCluster.scriptFlush(this.duic.getKey(getKeyPos(DistributedUniqueIdConf.KEY_MODE.KEY_NEXT)).getBytes(Const.UTF8));
            }
            luaShaMap.clear();
        }
        
        /**
         * 默认生成18位的序列
         *
         * @param seqName
         *            字段名称、表名
         * @return
         * @throws Exception 
         * @throws NumberFormatException 
         * @throws RetryException 
         * @throws ExecutionException 
         */
        public Long nextLong() throws NumberFormatException, ExecutionException, RetryException {
            return Long.parseLong(nextString());
        }
        
        /**
         * 自定义生成序列的位数
         *
         * @param seqLength
         * @return
         * @throws RetryException 
         * @throws ExecutionException 
         * @throws Exception 
         */
        public String nextString() throws ExecutionException, RetryException {
            if(duic.getIdsCacheNum()<=1) {
                curKey = duic.getKey(getKeyPos());
                return retryer.call(buildRetryTaskCall);
            }
            //下面的是多个id缓存的场景处理
            if(idsCacheQueue.isEmpty()) {//缓存的ids用完
                curKey = duic.getKey(getKeyPos());
                String maxId = retryer.call(buildRetryTaskCall);
                long maxId18;
                String maxIdPrefix18="";
                if(maxId.length()>=19) {//为了防止溢出，先截取再计算
                    maxId18=Long.parseLong(maxId.substring(maxId.length()-18));
                    maxIdPrefix18=maxId.substring(0,maxId.length()-18);
                }else {
                    maxId18=Long.parseLong(maxId);
                }
                //按从小到大的顺序压入队列，实现FIFO
                for(int i=duic.getIdsCacheNum()-1;i>=0;i--) {
                    idsCacheQueue.add(maxIdPrefix18+String.valueOf(maxId18-i*duic.getKeyNum()));
                }
                return idsCacheQueue.poll();
            }else {
                return idsCacheQueue.poll();
            }            
        }
        
        /**
         * 生成序列的主方法
         *
         * @param seqKey
         *            redis存入的key
         * @param seqLength
         *            序列长度
         * @param initValue
         *            序列初始值后缀
         * @return
         */
        private String innerNext(String seqKey) {
            String sequence = String.valueOf(jedisCluster.incrBy(seqKey.getBytes(Const.UTF8), trueKeyNumToServer ));
            curLuaSha=null;
            if (sequence.length() < duic.getKeyLength()) {
                curLuaSha=luaShaMap.get(seqKey);
                try {
                    if(curLuaSha==null) {
                        curLuaSha=jedisCluster.scriptLoad(lua.getBytes(Const.UTF8), seqKey.getBytes(Const.UTF8));
                        luaShaMap.put(seqKey,  curLuaSha);
                    }
                } catch (Exception e) {
                    curLuaSha=jedisCluster.scriptLoad(lua.getBytes(Const.UTF8), seqKey.getBytes(Const.UTF8));
                    luaShaMap.put(seqKey,  curLuaSha);
                    logger.error("redis node has no script!{}",e);
                }
                sequence = new String(byte[].class.cast(jedisCluster.evalsha(curLuaSha, 
                        1, seqKey.getBytes(Const.UTF8), 
                        String.valueOf(pos).getBytes(Const.UTF8), 
                        String.valueOf(trueKeyNumToServer).getBytes(Const.UTF8),
                        String.valueOf(duic.getKeyLength()).getBytes(Const.UTF8),
                        String.valueOf(duic.getValueMode().ordinal()).getBytes(Const.UTF8),
                        String.valueOf(duic.getTimeZone()).getBytes(Const.UTF8)
                        )));
            }
            if(duic.getValueMode()==DistributedUniqueIdConf.VALUE_MODE.VALUE_DAILY) {//按天
                long nowTime=Long.parseLong(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS))/1000000%1000000;
                long seqDate=Long.parseLong(sequence.substring(0,6));
                if(seqDate<nowTime) {
                    try {
                        if(curLuaSha==null) {
                            curLuaSha=jedisCluster.scriptLoad(lua.getBytes(Const.UTF8), seqKey.getBytes(Const.UTF8));
                            luaShaMap.put(seqKey,  curLuaSha);
                        }
                    } catch (Exception e) {
                        curLuaSha=jedisCluster.scriptLoad(lua.getBytes(Const.UTF8), seqKey.getBytes(Const.UTF8));
                        luaShaMap.put(seqKey,  curLuaSha);
                        logger.error("redis node has no script!{}",e);
                    }
                    logger.info("key is need another day..");
                    sequence = new String(byte[].class.cast(jedisCluster.evalsha(curLuaSha, 
                            1, seqKey.getBytes(Const.UTF8), 
                            String.valueOf(pos).getBytes(Const.UTF8), 
                            String.valueOf(trueKeyNumToServer).getBytes(Const.UTF8),
                            String.valueOf(duic.getKeyLength()).getBytes(Const.UTF8),
                            String.valueOf(duic.getValueMode().ordinal()).getBytes(Const.UTF8),
                            String.valueOf(duic.getTimeZone()).getBytes(Const.UTF8)
                            )));
                }
            }
            return sequence;
        }
        
        /**
         * 获取lua脚本文件
         *
         * @param filename
         * @return
         */
        private String getScriptContent(String filename) {
          if (StringUtil.isBlank(filename)) {
            return "";
          }
          InputStream is = null;
          BufferedInputStream fis = null;
          InputStreamReader isr = null;
          BufferedReader reader = null;
          StringBuilder script = new StringBuilder();
          try {
            is = this.getClass().getClassLoader().getResourceAsStream(filename);
            if(is==null) {
                logger.error("Not Exists! scriptResource={}", filename);
                throw new BillException(BillException.BillExceptionENUM.CONFIG_IS_ERROR,"Not Exists! scriptResource="+filename);
            }
            fis = new BufferedInputStream(is);
            isr = new InputStreamReader(fis, Const.UTF8);
            // 用5M的缓冲读取文本文件
            reader = new BufferedReader(isr, 5 * 1024 * 1024);
            String line;
            while ((line = reader.readLine()) != null) {
              script.append(line + "\r\n");
              logger.info("{}==>{}",filename, line);
            }
            return script.toString();
          } catch (Exception e) {
              logger.error("ERROR:{}", e);
          } finally {
            try {
              if (null != is) {
                is.close();
              }
              if (null != fis) {
                fis.close();
              }
              if (null != isr) {
                fis.close();
              }
              if (null != reader) {
                fis.close();
              }
            } catch (IOException e) {
              logger.error("ERROR:{}", e);
            }
          }
          return "";
        }
        
        public String getLua() {
            return lua;
        }
        
        ///内容是map<curKey,luaSha>
        public Map<String, byte[]> getLuaShaMap() {
            return luaShaMap;
        }
        
        ///当前key对应的下标
        public int getPos() {
            return pos;
        }
        
        public String getCurKey() {
            return curKey;
        }
        
        public DistributedUniqueIdConf getDistributedUniqueIdConf() {
            return duic;
        }
    }
    
    ///每次都产生新的一个序列对象,如果多个线程要用同一个序列，请使用这个函数
    public static synchronized DistributedUniqueId newDistributedUniqueId(String duidName) throws BillException {
        DistributedUniqueId duid=new DistributedUniqueId(getConf(duidName));
        duidObjMap.put(duidName, duid);
        return duid;
    }
    
    ///获取一个duidName对应的全局对象，如果多个线程的序列不会相同，请使用这个函数，也可以使用上面那个函数
    public static DistributedUniqueId globalDistributedUniqueId(String duidName) throws BillException {
        synchronized(DistributedUniqueIdFactory.class) {
            if(duidObjMap.containsKey(duidName)) {
                return duidObjMap.get(duidName);
            }
            DistributedUniqueId duid=new DistributedUniqueId(getConf(duidName));
            duidObjMap.put(duidName, duid);
            return duid;
        }
    }
    
    ///获取配置信息，并返回duidName对应的配置信息
    private static synchronized DistributedUniqueIdConf getConf(String duidName) {
        if(duidConfMap.isEmpty()) {//第一次加载配置文件
            Properties properties = PropertiesUtil.getProperties(DISTRIBUTED_UNIQUE_ID_CONF);
            String value;
            value=properties.getProperty(DistributedUniqueIdConf.SEQ_LIST_NAME).trim();
            if(value!=null) {
                DistributedUniqueIdConf duic;
                for(String duid:value.split(",")) {
                    duic=new DistributedUniqueIdConf();
                    duic.setConf(properties,duid.trim());
                    logger.info("DistributedUniqueIdConf[{}]={}",duid,duic);
                    duidConfMap.put(duid.trim(),duic);
                }
                if(!duidConfMap.containsKey(DistributedUniqueIdConf.SEQ_KEY_NAME))
                    duidConfMap.put(DistributedUniqueIdConf.SEQ_KEY_NAME, new DistributedUniqueIdConf());
            }else {//没有配置，采用默认配置
                duidConfMap.put(DistributedUniqueIdConf.SEQ_KEY_NAME, new DistributedUniqueIdConf());
            }
        }
        DistributedUniqueIdConf duic=duidConfMap.get(duidName);
        if(duic==null) {
            duic=duidConfMap.get(DistributedUniqueIdConf.SEQ_KEY_NAME);
            duidConfMap.put(duidName,duic);
            logger.info("DistributedUniqueIdConf[{}]={}",duidName,duic);
        }
        return duic;
    }
}
