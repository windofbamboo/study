package com.ai.iot.bill.cb.base.entity;

import java.util.Arrays;

/**
 * 批价异常类
 * 
 * @author xue
 *
 */
public class RateException extends Exception {

	private static final long serialVersionUID = -3726153134037674242L;

	private RateExceptionEnum errorCode = RateExceptionEnum.SUCCESS;

	public enum RateExceptionEnum {
		SUCCESS, OUT_COLLECTOR_UNKNOWN, OUT_EXTERNAL_COLLECTORS_IS_NULL, OUT_EXTERNAL_COLLECTOR_NOT_EXISTS, OUT_EXTERNAL_TOPIC_COLLECTOR_NOT_EXISTS, KAFKA_IS_NULL, ZK_WRAPPER_IS_NULL, SORT_MQ_IS_NULL, ZK_PATH_NOT_EXISTS, ZK_PATH_ALREADY_EXISTS, EXTERNAL_IN_COLLECTOR_IS_NULL, REDIS_SORT_MQ_NOT_SUPPORT_COMMIT, KAFKA_MQ_NOT_SUPPORT_REMOVE_MESSAGE, MAX;
	}

	protected static final String exceptionMessage[] = { "成功", "未知的输出器", "输出的外部分发器为空", "输出的外部分发器不存在", "主题对应的输出外部分发器不存在",
			"KAFKA对象为空", "ZK对象为空", "有序队列为空", "ZK路径不存在", "ZK路径已经存在", "没有消息采集器", "redis的有序队列不支持批量提交删除",
			"kafka队列不支持删除随机的单个消息" };

	public RateExceptionEnum getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return super.getLocalizedMessage();
	}

	public RateException(RateExceptionEnum eInt) {
		super(exceptionMessage[eInt.ordinal()]);
		this.errorCode = eInt;
		checkSelf();
	}

	public RateException(RateExceptionEnum eInt, String... eMsgs) {
		super("[" + Arrays.toString(eMsgs) + "]" + exceptionMessage[eInt.ordinal()]);
		this.errorCode = eInt;
		checkSelf();
	}

	private void checkSelf() {
		if (RateExceptionEnum.MAX.ordinal() != RateException.exceptionMessage.length) {
			throw new RuntimeException("错误信息定义有问题,请核查代码,错误代码和错误信息必须匹配!");
		}
	}
}
