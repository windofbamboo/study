package com.ai.iot.bill.common.mdb;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.util.Const;

import redis.clients.jedis.exceptions.JedisDataException;

public class MdbUtil {
	private static final Logger logger = LoggerFactory.getLogger(MdbUtil.class);

	private static final byte[] SHA_UPDATE_WITH_VER = "cf9fbc7b3707a934abb45835608becb6326ec8d7"
			.getBytes(Const.UTF8);
	private static final byte[] LUA_UPDATE_WITH_VER = "local v=redis.call('hget',KEYS[1],'VER');if(not v or v==ARGV[1]) then return redis.call('hmset',KEYS[1],'VER',ARGV[2],ARGV[3],ARGV[4],ARGV[5],ARGV[6],ARGV[7],ARGV[8],ARGV[9],ARGV[10],ARGV[11],ARGV[12],ARGV[13],ARGV[14]);end;return nil;"
			.getBytes(Const.UTF8);
	
	public static final int LUA_UPDATE_WITH_VER_PARAMS_LENGTH=15;
	
	public static int updateWithVersion(CustJedisCluster jc,List<byte[]> aparams) {
		byte[][] params=new byte[aparams.size()][];
		aparams.toArray(params);
		
		return updateWithVersion(jc,params);
	}
	
	/* params[0] : 要更新的key
	 * params[1] : 旧版本
	 * params[2] : 新版本
	 * params[3-14] : 要更新的多个hash字段，格式："field" "value" "field" "value"...，不足对的可用占位字段如:"tmp" ""
	 */
	public static int updateWithVersion(CustJedisCluster jc,byte[]... aparams) {
		if(aparams.length<5 || aparams.length>LUA_UPDATE_WITH_VER_PARAMS_LENGTH || (aparams.length%2!=1)){
			logger.error("updateWithVersion params length must >=5 and <= {}, now is {}.",LUA_UPDATE_WITH_VER_PARAMS_LENGTH,aparams.length);
			return Const.ERROR;
		}		
		
		byte[][] params;
		if(aparams.length==LUA_UPDATE_WITH_VER_PARAMS_LENGTH){
			params=aparams;
		}else{
			params=new byte[LUA_UPDATE_WITH_VER_PARAMS_LENGTH][];
			System.arraycopy(aparams, 0, params, 0, aparams.length);
			for(int i=aparams.length;i<LUA_UPDATE_WITH_VER_PARAMS_LENGTH;i+=2){
				params[i] = "tmp".getBytes(Const.UTF8);
				params[i+1] = "".getBytes(Const.UTF8);
			}
		}		
		
		int i = 0;
		while (i < 2) {
			try {
				String ret = new String((byte[])jc.evalsha(SHA_UPDATE_WITH_VER, 1,params),Const.UTF8);
				
				if("OK".equalsIgnoreCase(ret)){
					return Const.OK;
				}else{
					return Const.FAIL;
				}				
			} catch (JedisDataException e) {				
				if(e.getMessage().substring(0, 8).compareToIgnoreCase("NOSCRIPT")==0)
				{
					jc.scriptLoad(LUA_UPDATE_WITH_VER,params[0]);
				}else{
					logger.error("evalsha function return error:{}",e.getMessage());
				}
				i++;
			}
		}
		return Const.ERROR;
	}
	
	private MdbUtil() {
	    throw new IllegalStateException("Utility class");
	}
}
