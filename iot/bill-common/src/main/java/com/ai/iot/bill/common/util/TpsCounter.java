package com.ai.iot.bill.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于打印处理性能,默认每分钟打印一次
 * 用法:
 * TpsCounter tps = new TpsCounter("xxxx",this.class);
 * tps.getIntervalCheck().setIntervalMs(3000);//非必需,3秒,默认为60s
 * xxxx();
 * tps.count(); 如果一次性处理多条可以是tps.count(n);
 * tps.cleanup();//退出时可以调用,将打印剩余的信息,否则最后一个时间间隔的信息可能没有了
 * @author qianwx
 */
public class TpsCounter implements Serializable {

  private static final long serialVersionUID = 2177944366059817622L;
  
  /**总调用次数*/
  private AtomicLong 		totalTimes            = new AtomicLong(0);
  
  /**总记录数*/
  private AtomicLong        totalValues           = new AtomicLong(0);

  /**当前统计周期内的调用次数*/
  private AtomicLong        times            = new AtomicLong(0);
  
  /**当前统计周期内的每次调用的平均记录数*/
  private AtomicLong        values           = new AtomicLong(0);

  /**时间间隔器*/
  private IntervalCheck intervalCheck;

  /**被性能测试的对象id*/
  private final String id;
  
  /**打印日志的对象*/
  private final Logger logger;

  /**按毫秒计的时间间隔*/
  public static class IntervalCheck implements Serializable {
	    private static final long serialVersionUID = 8952971673547362883L;
	    long lastCheck = 0;
	    long startCheck=0;
	    // default interval is 1 second
	    long intervalMillis = 1000;

	    ///返回毫秒差
	    public long checkAndGetMillis() {
	        long now = System.currentTimeMillis();

	        synchronized (this) {
	            if (now >= intervalMillis + lastCheck) {
	                long pastSecondMillis = now - lastCheck;//((double) (now - lastCheck)) / 1000;
	                lastCheck = now;
	                return pastSecondMillis;
	            }
	        }
	        return 0;
	    }
	    
	    ///返回的是毫秒
	    public long getMillis() {
	        long now = System.currentTimeMillis();
	        synchronized (this) {
	               long pastSecondMillis = now - lastCheck;
	               lastCheck=now;
	               return pastSecondMillis;
	        }
	    }

	    ///返回的是总毫秒数
	    public long getTotalMillis() {
	    	return System.currentTimeMillis() - startCheck;
	    }
	    
	    public long getInterval() {
	        return intervalMillis / 1000;
	    }

	    public long getIntervalMs() {
	        return intervalMillis;
	    }

	    public void setInterval(long interval) {
	        this.intervalMillis = interval * 1000;
	    }

	    public void setIntervalMs(long intervalMillis) {
	        this.intervalMillis = intervalMillis;
	    }

	    public void adjust(long addTimeMillis) {
	        lastCheck += addTimeMillis;
	    }

	    public boolean isStart() {
	        return lastCheck != 0;
	    }

	    public void start() {
	    	startCheck = lastCheck = System.currentTimeMillis();
	    }

	    public long getLaskCheckTime() {
	        return lastCheck;
	    }
	}
  public TpsCounter() {
    this("", TpsCounter.class);
  }

  public TpsCounter(String id) {
    this(id, TpsCounter.class);
  }

  public TpsCounter(Class<?> tclass) {
    this("", tclass);
  }

  public TpsCounter(String id, Class<?> tclass) {
    this.id = id;
    this.logger = LoggerFactory.getLogger(tclass);

    intervalCheck = new IntervalCheck();
    intervalCheck.setInterval(60);
    intervalCheck.start();
  }

  public void count(long value) {
    totalTimes.incrementAndGet();
    totalValues.addAndGet(value);
    times.incrementAndGet();
    values.addAndGet(value);

    dump(intervalCheck.checkAndGetMillis());
    return ;
  }

  public void count() {
	  count(1L);
  }

  public void cleanup() {
	  dump(intervalCheck.getMillis());
  }

  public IntervalCheck getIntervalCheck() {
      return intervalCheck;
  }
  
  public void dump(long passMillis) {
	  long totalPassMillis=getIntervalCheck().getTotalMillis();
	  StringBuilder sb = new StringBuilder();
      sb.append("{tpsid:");
      sb.append(id);
      sb.append(", calls:" + times.get());
      sb.append(", recs:" + values.get());
      sb.append(", delay_ms:" + passMillis);
      sb.append(", avg(call/s):" + (times.get() / passMillis/1000));
      sb.append(", avg(recs/call):" + ((double) values.get()) / times.get());
      sb.append(", total-calls:" + totalTimes.get());
      sb.append(", total-recs:" + totalValues.get());
      sb.append(", total-delay_ms:" + totalPassMillis);
      sb.append(", total-avg(call/s):" + (totalTimes.get() / totalPassMillis/1000));
      sb.append(", total-avg(recs/call):" + ((double) totalValues.get()) / totalTimes.get()+"}");
      logger.info(sb.toString());
  }
}