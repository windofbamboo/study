package com.ai.iot.mdb.common.rate;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.util.CdrConst;
import com.ai.iot.bill.common.util.CheckNull;

/**
 * 内存累计量读写类
 * @author xue
 *
 */
public class MdbTableRating extends MdbTable {
	private final static Logger logger = LoggerFactory.getLogger(MdbTableRating.class);

	private static final String VER = "VER";

	public static final int TABLE_TYPE_DEVICE_DATA = 0;
	public static final int TABLE_TYPE_DEVICE_SMS = 1;
	public static final int TABLE_TYPE_DEVICE_VOICE = 2;
	public static final int TABLE_TYPE_TEST_DATA = 3;
	public static final int TABLE_TYPE_TEST_SMS = 4;
	public static final int TABLE_TYPE_TEST_VOICE = 5;
	public static final int TABLE_TYPE_POOL = 6;

	private List<String> fields = new ArrayList<>();
	private List<Integer> fieldsType = new ArrayList<>();
	private String cycleDate=null;
	private int tableType = 0;
	private String key=null;
	
	public MdbTableRating(int tableType){
		this.tableType = tableType;
	}
	
	/**
	 * 读取表类型
	 * @param bizType
	 * @param device
	 * @param test
	 * @return
	 */
	public static int getTableType(int bizType,boolean device,boolean test){
		int type = -1;
		
		if(device){
			if(test){
				switch(bizType){
				case CdrConst.BIZ_DATA:
					type = TABLE_TYPE_DEVICE_DATA;
					break;
				case CdrConst.BIZ_SMS:
					type = TABLE_TYPE_DEVICE_SMS;
					break;
				case CdrConst.BIZ_VOICE:
					type = TABLE_TYPE_DEVICE_VOICE;
					break;
				default:
					logger.error("Device cdr bizType is error:{}",bizType);
					break;
				}
			}else{
				switch(bizType){
				case CdrConst.BIZ_DATA:
					type = TABLE_TYPE_TEST_DATA;
					break;
				case CdrConst.BIZ_SMS:
					type = TABLE_TYPE_TEST_SMS;
					break;
				case CdrConst.BIZ_VOICE:
					type = TABLE_TYPE_TEST_VOICE;
					break;
				default:
					logger.error("Test cdr bizType is error:{}",bizType);
					break;
				}
			}
		}else{
			type = TABLE_TYPE_POOL;
		}
		
		return type;
	}
	
	/**
	 * 重置
	 * @param cycleId
	 * @param deviceId
	 * @param guid
	 * @param acctId
	 * @param poolId
	 * @return
	 */
	public boolean reset(String cycleId, String deviceId, String guid, String acctId, String poolId) {		
		key = null;

		if (tableType == TABLE_TYPE_DEVICE_DATA) {
			if (!cycleId.equals(cycleDate)) {
				cycleDate = cycleId;

				fields.clear();
				fields.add("SUM-MON-" + cycleDate);
				fields.add("SUM-PRE");
				fields.add("SUM-FACTOR-" + cycleDate);
				fields.add("SUM-ZONE-" + cycleDate);
				fields.add("SUM-STAT");
				fields.add(VER);
				fields.add("GUID");
				
				fieldsType.clear();
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LONG);
				fieldsType.add(MdbTables.FIELD_TYPE_STRING);
			}
			
			fields.set(fields.size() - 1, guid);

			key = "430+" + deviceId;
		} else if (tableType == TABLE_TYPE_DEVICE_VOICE) {
			if (!cycleId.equals(cycleDate)) {
				cycleDate = cycleId;

				fields.clear();
				fields.add("SUM-MON-" + cycleDate);
				fields.add("SUM-PRE");
				fields.add("SUM-STAT");
				fields.add(VER);
				fields.add("GUID");

				fieldsType.clear();
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LONG);
				fieldsType.add(MdbTables.FIELD_TYPE_STRING);
			}
			
			fields.set(fields.size() - 1, guid);

			key = "410+" + deviceId;
		} else if (tableType == TABLE_TYPE_DEVICE_SMS) {
			if (!cycleId.equals(cycleDate)) {
				cycleDate = cycleId;

				fields.clear();
				fields.add("SUM-MON-" + cycleDate);
				fields.add("SUM-PRE");
				fields.add("SUM-STAT");
				fields.add(VER);
				fields.add("GUID");

				fieldsType.clear();
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LONG);
				fieldsType.add(MdbTables.FIELD_TYPE_STRING);
			}
			
			fields.set(fields.size() - 1, guid);

			key = "420+" + deviceId;
		} else if(tableType == TABLE_TYPE_TEST_DATA 
				|| tableType == TABLE_TYPE_TEST_VOICE 
				|| tableType == TABLE_TYPE_TEST_SMS){
			if(fields.isEmpty()){
				fields.add("AMOUNT");
				fields.add(VER);				

				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LONG);
			}
			
			if (tableType == TABLE_TYPE_TEST_DATA) {
				key = "430+" + deviceId;
			} else if (tableType == TABLE_TYPE_TEST_VOICE) {
				key = "431+" + deviceId;
			} else if (tableType == TABLE_TYPE_TEST_SMS) {
				key = "432+" + deviceId;
			}
		} else if(tableType == TABLE_TYPE_POOL){
			if(fields.isEmpty()){
				fields.add("SUM-PRE");
				fields.add(VER);
				fields.add("GUID");

				fieldsType.add(MdbTables.FIELD_TYPE_LIST);
				fieldsType.add(MdbTables.FIELD_TYPE_LONG);
				fieldsType.add(MdbTables.FIELD_TYPE_STRING);
			}
			
			fields.set(fields.size() - 1, guid);
			
			key = "400+" + acctId+"+"+poolId;
		}
		
		if (CheckNull.isNull(key)) {
			return false;
		}

		return true;
	}

	/**
	 * 读取FIELD列表
	 */
	@Override
	public List<String> getFields() {		
		return fields;
	}

	/**
	 * 读取FIELD类型
	 */
	@Override
	public List<Integer> getFieldsType() {
		return fieldsType;
	}

	/**
	 * 返回表类型
	 * @return
	 */
	public int getTableType() {
		return tableType;
	}

	/**
	 * 返回账期
	 * @return
	 */
	public String getCycleId() {
		return cycleDate;
	}

	@Override
	public Class<?> getFieldClazz(String fieldName) {
		return null;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getMdbTableKeyId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 重置
	 * @param deviceId
	 * @param cycId
	 * @return
	 */
	public boolean reset(String deviceId,int cycId){
		key = null;
		fields.add("SUM-ZONE-" + cycId);
		fieldsType.add(MdbTables.FIELD_TYPE_LIST);
		key = "430+" + deviceId;
		if (CheckNull.isNull(key)) {
			return false;
		}

		return true;
	}
}
