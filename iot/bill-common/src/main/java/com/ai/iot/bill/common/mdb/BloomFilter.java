package com.ai.iot.bill.common.mdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.param.BaseParamDao;
import com.ai.iot.bill.common.param.ParamBean;
import com.ai.iot.bill.common.util.Const;

import redis.clients.jedis.exceptions.JedisDataException;

public class BloomFilter implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(BloomFilter.class);

    private static final long serialVersionUID = -2503099163448027063L;

    private int modValue = -1;

    // 2的24次方
    private static final int DEFAULT_SIZE = 2 << 25;

    // 构造不同因子，用于不同散列函数
    private static final int[] seeds = new int[] { 3, 11, 13, 37 };
    
    private static final byte[] shaOfflineCdrRepeatExclu = "0a937358997e5f30aa9eb7d1400531c35aaa7c68".getBytes();

    private static final byte[] luaOfflineCdrRepeatExcluScript = "local flag = true\nfor i = 3, #ARGV do\nif(redis.call('GETBIT', KEYS[1], ARGV[i]) == 0) then\nflag = false\nredis.call('SETBIT', KEYS[1], ARGV[i], 1)\nend\nend\nlocal billKey = '{'..tostring(KEYS[1])..'}GUID'\nif(flag == false) then\nredis.call('SADD', billKey, ARGV[1])\nreturn 0\nelse\nif(tonumber(ARGV[2]) == 0) then\nreturn 1\nelse\nlocal ishit = redis.call('SISMEMBER', billKey, ARGV[1])\nif(ishit == 1) then\nreturn 0\nelse\nreturn 1\nend\nend\nend\n"
            .getBytes();

    public BloomFilter() {
        // 初始化mod值
        modValue = getModValue();
    }
    
    public static int getModValue(){
    	ParamBean bean = BaseParamDao.getParamByTypeAndName("BLOOMFILTER", "SETTING");
        if (bean != null) {
            return Integer.parseInt(bean.getParamValue2());
        }else{
        	return Const.ERROR;
        }
    }
    
    public static String getFilterKey(String cdrStartDate,String guid,int modValue){
        return "600+" + cdrStartDate + "+" + (hash(DEFAULT_SIZE, seeds[0], guid) % modValue);
    }

    /**
     * @Description 排重原理： 重单产生的原因：1）交换机发送了两次相同的话单文件。预处理生成两次的GUID不一样。
     *              2）内部计费错单处理两次，此时前后的GUID相同。
     *              排重根据fid进行处理，判断一个话单是否重复的标准就是话单的几个维度。
     *              GUID取模后生成key，用于分散存储，以突破redis setbit 512M限制
     * 
     * @author "zhangfeng"
     * @param cdrStartDate
     * @param guid
     *            内部生成的一个id。
     * @param fid
     *            根据话单属性拼出的一个ID，用于排重。包括：话单开始时间，结束时间，通话时间，号码等属性。
     * @repeatTimes 重发次数
     * @return 返回：-1=出错，0=不存在，1=存在
     */

    public int bloomFilter(CustJedisCluster jc, String cdrStartDate, String guid, String fid, int repeatTimes) {
        int[] pos = new int[seeds.length];// 记录bit

        for (int i = 0; i < seeds.length; i++) {
            pos[i] = hash(DEFAULT_SIZE, seeds[i], fid);
        }

        String key = getFilterKey(cdrStartDate,guid,modValue);
        /*
         * lua脚本 参数 key params: guid repeatTimes pos0....posi
         * 
         * 1）如果值为key的pos[0]...pos[i]上有一个0，说明不存在，那么： 1、pos[0]...pos[i]均置为1.
         * 2、在{key}GUID上SADD guid. 3、返回0
         * 
         * 2）如果值为key的pos[0]...pos[i]上均为1，说明可能存在，那么： 判断重发次数 1、等于0,，返回1.
         * 2、大于0，加判{key}GUID，若存在，说明处理中断，不是重单，返回0；若不存在，说明是重单，返回1。
         */
        List<byte[]> keys = new ArrayList<>();
        keys.add(key.getBytes(Const.UTF8));

        List<byte[]> argvs = new ArrayList<>();
        argvs.add(guid.getBytes(Const.UTF8));
        
        argvs.add(String.valueOf(repeatTimes).getBytes(Const.UTF8));
        for (int i = 0; i < seeds.length; i++) {
            argvs.add(String.valueOf(pos[i]).getBytes(Const.UTF8));
        }

        int i = 0;
        int ret = Const.ERROR;
        while (i < 2) {
            try {
                Object r = jc.evalsha(shaOfflineCdrRepeatExclu, keys, argvs);
                if(null != r){
                	ret = Integer.parseInt(r.toString());
                }
                break;
            } catch (JedisDataException e) {
                if (e.getMessage().substring(0, 8).compareToIgnoreCase("NOSCRIPT") == 0) {
                     jc.scriptLoad(luaOfflineCdrRepeatExcluScript, key.getBytes());
                    
                } else {
                    logger.error("bloomFilter error:{}", e.getMessage());
                }
                i++;
            }
        }

        return  ret;
    }

    // hash值通过数字个数和数字散列后得到值
    public static int hash(int cap, int seed, String value) {
        int result = 0;
        int len = value.length();
        for (int i = 0; i < len; i++) {
            result = seed * result + value.charAt(i);
        }
        return (cap - 1) & result;
    }
}
