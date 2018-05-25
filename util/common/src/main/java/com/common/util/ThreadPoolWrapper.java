package com.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolWrapper {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolWrapper.class);
    protected ExecutorService fixedThreadPool=null;
    protected int threadNum;
    protected int threadWaitingSleepMillSecs=1000;
    protected List<Callable<?>> taskList;
    protected List<Future<?>> futureList;
    protected boolean isNotSubmitTask=false;
    
    public ThreadPoolWrapper() {
        this(5);
    }
    public ThreadPoolWrapper(int threadNum) {
        this.threadNum=threadNum;
        taskList=new ArrayList<>();
        futureList=new ArrayList<>();
    }
    public void start() {
        logger.warn("isNotSubmitTask={}",isNotSubmitTask);
        if(fixedThreadPool==null) {
            fixedThreadPool=Executors.newFixedThreadPool(threadNum);
        }
    }
    public void addTask(Callable<?> call) {
        if(isNotSubmitTask) {
            taskList.add(call);
            return;
        }
        if(fixedThreadPool==null) {
            throw new RuntimeException("call start() first");
        }
        futureList.add(fixedThreadPool.submit(call));
        taskList.add(call);
    }
    public void clearTasks() {
        futureList.clear();
        taskList.clear();
    }
    public boolean isAllTaskDone() {
        ThreadPoolExecutor tpe=(ThreadPoolExecutor)fixedThreadPool;
        return tpe.getTaskCount()==tpe.getCompletedTaskCount();
    }
    public boolean waitAllTaskDone() {
        ThreadPoolExecutor tpe=(ThreadPoolExecutor)fixedThreadPool;
        while( tpe.getTaskCount()!=tpe.getCompletedTaskCount()) {
            logger.info("TaskCount={},CompletedTaskCount={},ActiveCount Thread={},And Sleep({}ms)...",tpe.getTaskCount(),tpe.getCompletedTaskCount(),tpe.getActiveCount(),threadWaitingSleepMillSecs);
            try {
                Thread.sleep(threadWaitingSleepMillSecs);
            } catch (InterruptedException e) {
                logger.error("ERROR:",e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }
    public void stop() {
        fixedThreadPool.shutdown();
        // 关闭线程池
        logger.info("Start to waiting tasks shutdown...");
        try {
            while (!fixedThreadPool.isTerminated()) {
                ThreadPoolExecutor tpe=(ThreadPoolExecutor)fixedThreadPool;
                logger.info("TaskCount={},CompletedTaskCount={},ActiveCount Thread={},And Sleep({}ms)...",tpe.getTaskCount(),tpe.getCompletedTaskCount(),tpe.getActiveCount(),threadWaitingSleepMillSecs);
                Thread.sleep(threadWaitingSleepMillSecs);
            }
            // fixedThreadPool.awaitTermination(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("ERROR:",e);
            Thread.currentThread().interrupt();
        }
    }
    public List<Object> getResultList() throws Exception {
        List<Object> resultList=new ArrayList<>();
        for (Future<?> f : futureList) {
            resultList.add(f.get());
        }
        return resultList;
    }
    public int getThreadNum() {
        return threadNum;
    }
    
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
    
    public void setThreadSleepMillSecs(int threadSleepMillSecs) {
        this.threadWaitingSleepMillSecs = threadSleepMillSecs;
    }
    
    public List<Callable<?>> getTaskList() {
        return taskList;
    }

    public List<Future<?>> getFutureList() {
        return futureList;
    }
    public boolean isNotSubmitTask() {
        return isNotSubmitTask;
    }
    public void setNotSubmitTask(boolean isNotSubmitTask) {
        this.isNotSubmitTask = isNotSubmitTask;
    }
    
}
