package com.ai.iot.bill.common;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.util.BillException;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.common.util.DistributedUniqueIdFactory;
import com.ai.iot.bill.common.util.DistributedUniqueIdFactory.DistributedUniqueId;
import com.github.rholder.retry.RetryException;

public class DistributedUniqueIdTest {
    public static class MyCallable implements Callable<Long>{
        private static Map<Long,DistributedUniqueId> duidMap=new ConcurrentHashMap<>();
        private Set<Long> ids=new HashSet<>();
        private Set<Long> sameIds=new HashSet<>();
        private DistributedUniqueId duid;
        private int taskId;
        private boolean isClear;
        private String duidName;
        public void setProperties(String duid,int taskId,boolean isClear) {
            this.taskId=taskId;
            this.isClear=isClear;
            this.duidName=duid;
        }
        private void init() {
            duid=duidMap.get(Thread.currentThread().getId());
            if(duid==null) {
                try {
                    System.out.println("newDistributedUniqueId()..."+Thread.currentThread().getId());
                    duid=DistributedUniqueIdFactory.newDistributedUniqueId(duidName);
                    duidMap.put(Thread.currentThread().getId(), duid);
                } catch (BillException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public Long call() throws Exception {
            init();
            if(isClear) {
                duid.clearScripts();
                return -1L;
            }
            Long id=0L;
            try {
                id = duid.nextLong();
                System.out.println("ID={taskid:"+taskId+",threadid:"+Thread.currentThread().getId()+",ID:" +id+",curKey:"+duid.getCurKey()+"}");
            } catch (NumberFormatException | ExecutionException | RetryException e) {
                e.printStackTrace();
                return -1L;
            }
            if(ids.contains(id))
                sameIds.add(id);
            else
                ids.add(id);
            return id;
        }
        
        public Set<Long> getIds(){
            return ids;
        }
        public Set<Long> getSameIds(){
            return sameIds;
        }
        public int getTaskId() {
            return taskId;
        }
        public int getKeyNum() {
            return duid.getDistributedUniqueIdConf().getKeyNum();
        }
    }

    @BeforeClass
    public static void init() throws Exception {        
    }
    
    @Before
    public void setUp() throws Exception {        
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testDuid(){
        int taskSize=1000;
        int threadNum=5;
        int keynum;
        String duidName="testduid";
        
        ///获取并发的key个数
        DistributedUniqueId duid;
        try {
            duid = DistributedUniqueIdFactory.newDistributedUniqueId(duidName);
        } catch (BillException e1) {
            e1.printStackTrace();
            return;
        }
        duid.clearScripts();
        keynum=duid.getDistributedUniqueIdConf().getKeyNum();
        
         //创建一个线程池
        ExecutorService newFixedThreadPool=Executors.newFixedThreadPool(threadNum);
        
        // 创建清理的任务
        System.out.println("Start to clear scripts...");
        MyCallable c;
        for (int i = 0; i < keynum; i++) {
            c = new MyCallable();
            c.setProperties(duidName,i,true);
            newFixedThreadPool.submit(c);
        }        
        newFixedThreadPool.shutdown();
        // 关闭线程池
        System.out.println("Start to waiting clear tasks shutdown...");
        try {
            while(!newFixedThreadPool.isTerminated()) {
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //正式开始任务
        System.out.println("Start to generator duid...");
        List<Future> list = new ArrayList<>();
        List<MyCallable> myTasksList = new ArrayList<>();
        newFixedThreadPool=Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < taskSize; i++) {
            c = new MyCallable();
            c.setProperties(duidName,i,false);
            // 执行任务并获取Future对象
            list.add( newFixedThreadPool.submit(c));
            myTasksList.add((MyCallable) c);
        }
        
        newFixedThreadPool.shutdown();
        // 关闭线程池
        System.out.println("Start to waiting generator tasks shutdown...");
        try {
            while(!newFixedThreadPool.isTerminated()) {
                Thread.sleep(1);
            }
            //newFixedThreadPool.awaitTermination(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        
        // 获取所有并发任务的运行结果
        System.out.println("Start to get result...");
        int idTotal=0;
        int sameIdTotal=0;
        Set<Long> sameIds=new HashSet<>();
//        for (Future f : list) {
//            // 从Future对象上获取任务的返回值，并输出到控制台
//            try {
//                System.out.println("result from thread=" +(Long) f.get());
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//                continue;
//            }
//        }
        for (MyCallable mc : myTasksList) {
            idTotal = idTotal + mc.getIds().size();
            sameIdTotal = sameIdTotal + mc.getSameIds().size();
            sameIds.addAll(mc.getSameIds());
        }
        for(Long id:sameIds) {
            System.out.println("same id=" +id);
        }
        System.out.println("ids num=" +idTotal);
        System.out.println("same ids num=" +sameIds.size());
        boolean r=(sameIds.isEmpty()&&idTotal==taskSize);
        System.out.println("result=" + String.valueOf(r));        
        assertTrue(r);
    }

    @Test
    public void testDuidByCache(){
        String duidName="testduidcache";
        
        ///获取并发的key个数
        DistributedUniqueId duid;
        try {
            duid = DistributedUniqueIdFactory.newDistributedUniqueId(duidName);
        } catch (BillException e) {
            e.printStackTrace();
            return;
        }
        if(duid.getDistributedUniqueIdConf().getIdsCacheNum()<=1) {
            assertTrue(false);
            return;
        }
        //获取新的id,连续的几个id是连续的且当前key未变化
        String minKey=null;
        String minId=null;
        String minIdPrefix18=null;
        long minId18=-1;
        String curId=null;
        boolean result=true;
        String curKey=null;
        for(int i=0;i<duid.getDistributedUniqueIdConf().getIdsCacheNum();i++) {
            if(i==0) {
                try {
                    minId=duid.nextString();
                    minKey=duid.getCurKey();
                    if(minId.length()>=19) {
                        minId18=Long.parseLong(minId.substring(minId.length()-18));
                        minIdPrefix18=minId.substring(0, minId.length()-18);
                    }else {
                        minId18=Long.parseLong(minId);
                        minIdPrefix18="";
                    }
                    System.out.println("minId18="+minId18);
                    System.out.println("minIdPrefix18="+minIdPrefix18);
                    System.out.println("curKey="+minKey+",curId="+minId);
                } catch (NumberFormatException | ExecutionException | RetryException e) {
                    e.printStackTrace();
                    continue;
                }
            }else {
                try {
                    curId=duid.nextString();
                } catch (ExecutionException | RetryException e) {
                    e.printStackTrace();
                    assertTrue(false);
                    return;
                }
                curKey=duid.getCurKey();
                System.out.println("curKey="+curKey+",curId="+curId+",i="+i);
                if(!curId.equals(minIdPrefix18+String.valueOf(minId18+i*duid.getDistributedUniqueIdConf().getKeyNum()))) {
                    result=false;
                    assertTrue(false);
                    return;
                }
                if(!curKey.equals(minKey)) {
                    result=false;
                    assertTrue(false);
                    return;
                }
            }
        }
        //超过后，缓存获取新的ids，当前的key变化了，且id还是连续的
        int num=duid.getDistributedUniqueIdConf().getIdsCacheNum();
        for(int i=0;i<duid.getDistributedUniqueIdConf().getKeyNum()*duid.getDistributedUniqueIdConf().getIdsCacheNum();i++) {
            try {
                curId=duid.nextString();
            } catch (ExecutionException | RetryException e) {
                e.printStackTrace();
                assertTrue(false);
                return;
            }
            curKey=duid.getCurKey();
            System.out.println("curKey="+curKey+",curId="+curId);
            //后续相同的key的ids应该是连续的。
            if(curKey.equals(minKey)&&!curId.equals( minIdPrefix18+String.valueOf(minId18+num*duid.getDistributedUniqueIdConf().getKeyNum()))) {
                result=false;
                assertTrue(false);
                return;
            }
            if(curKey.equals(minKey)) {
                num++;
            }
            if(!curKey.equals(minKey)&&curId.equals( minIdPrefix18+String.valueOf(minId18+duid.getDistributedUniqueIdConf().getIdsCacheNum()*duid.getDistributedUniqueIdConf().getKeyNum()))) {
                result=false;
                assertTrue(false);
                return;
            }
        }
        
        System.out.println("result="+result);
        assertTrue(result);
    }
    @Test
    public void testDuidByDaily(){
        String earlyduid="17082913595900001";
        long earlydate=Long.parseLong(earlyduid.substring(0, 6));
        String duidName="testduiddaily";
        
        ///获取并发的key个数
        DistributedUniqueId duid;
        try {
            duid = DistributedUniqueIdFactory.newDistributedUniqueId(duidName);
        } catch (BillException e) {
            e.printStackTrace();
            return;
        }
        
        //连接redis
        CustJedisCluster jc = RedisMgr.getJedisCluster(duid.getDistributedUniqueIdConf().getRedisConnType());
        if (jc == null) {
            System.out.println("connType=" + duid.getDistributedUniqueIdConf().getRedisConnType());
            assertTrue(false);
            System.exit(10);
        }
        //获取key信息
        Set<String> keyList=new HashSet<>();
        for(int i=0;i<duid.getDistributedUniqueIdConf().getKeyNum();i++) {
            try {
                duid.nextLong();
            } catch (NumberFormatException | ExecutionException | RetryException e) {
                e.printStackTrace();
                continue;
            }
            keyList.add(duid.getCurKey());
            System.out.println("key="+duid.getCurKey());
        }
        //手工设置序列ID值为早期的值
        for(String key:keyList) {
            jc.set(key.getBytes(Const.UTF8), earlyduid.getBytes(Const.UTF8));
            System.out.println("key["+key+"]="+earlyduid);
        }
        //获取新的id
        String id;
        boolean result=true;
        boolean oneresult;
        long nowDate=Long.parseLong(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS).substring(2,8));
        System.out.println("nowDate="+nowDate);
        for(int i=0;i<duid.getDistributedUniqueIdConf().getKeyNum();i++) {
            try {
                id=duid.nextString();
            } catch (NumberFormatException | ExecutionException | RetryException e) {
                e.printStackTrace();
                continue;
            }
            oneresult=false;
            if(Long.parseLong(id.substring(0,6))>earlydate&&nowDate==Long.parseLong(id.substring(0,6)))
                oneresult=true;
            if(!oneresult)
                result=false;
            System.out.println("newid:key["+duid.getCurKey()+"]="+id+"<"+oneresult+">");
        }
        System.out.println("result="+result);
        assertTrue(result);
    }
}
