package com.common.util;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MybatisUtil {

    private static final Logger logger = LoggerFactory.getLogger(MybatisUtil.class);

    private static Map<String,SqlSessionFactory> sessionFactoryMap = new HashMap<>();
    private final static ClassLoaderUtil classLoaderUtil=new ClassLoaderUtil();
    
    private static final int INIT = 0;
    private static final int UPDATE = 1;
    private static final int FAIL = 2;
    private static final int SUCESS = 3;
    private static AtomicInteger initTag = new AtomicInteger();

    public static SqlSession getSqlSession(String environment){

        init(null,environment);
        if (initTag.get() == SUCESS) {
            SqlSessionFactory factory = sessionFactoryMap.get(environment);
            return factory.openSession();
        } else {
            return null;
        }
    }

    public static SqlSession getSqlSession(final String model,String environment){

        init(model,environment);
        if (initTag.get() == SUCESS) {
            SqlSessionFactory factory = sessionFactoryMap.get(environment);
            return factory.openSession();
        } else {
            return null;
        }
    }

    private static boolean check(String environment){
        if(sessionFactoryMap.isEmpty()){
            return false;
        }
        return sessionFactoryMap.containsKey(environment);
    }

    private static void init(final String model,String environment){

        //初始化的判断
        if (initTag.compareAndSet(INIT, UPDATE)) {
            done(model,environment);
        }
        if (!check(environment)) {
            if (initTag.compareAndSet(SUCESS, UPDATE)) {
                done(model,environment);
            }
        }

        //等待更新线程完成
        for (; ; ) {
            if (initTag.get() == UPDATE) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            } else {
                break;
            }
        }
    }


    private static void done(final String model,final String environment) {

        String resource;
        if(model == null){
            resource = "mybatis-config.xml";
        }else{
            resource = model+"-mybatis-"+"config.xml";
        }
        
        InputStream inputStream = classLoaderUtil.getResourceAsStream(resource,MybatisUtil.class.getClassLoader());
        if(inputStream==null) {
        	logger.error("getResourceAsStream(resource) error : {}.{}",resource,environment);
            initTag.set(FAIL);
            return;
        }
        Properties properties = resSetProperties(model);

        SqlSessionFactory factory = null;
        try {
            factory = new SqlSessionFactoryBuilder().build(inputStream,environment,properties);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SqlSessionFactory build err: {}.{}",model,environment,e);
            initTag.set(FAIL);
        }
        sessionFactoryMap.put(environment,factory);
        try {
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("inputStream close err: {}.{}",model,environment,e);
        }
        initTag.set(SUCESS);
    }


    private static Properties resSetProperties(final String model){

        Properties ppt=null;
        String propertyFileName="";
        try {
            if(model == null){
                propertyFileName = "jdbc.properties";
            }else{
                propertyFileName = model+"-jdbc"+".properties";
            }
            ppt= PropertiesUtil.getProperties(propertyFileName);
        } catch (Exception e) {
            logger.error("jdbc.properties read file err: {}",propertyFileName,e);
        }
        if(ppt==null){
            return null;
        }

        List<String> firstList = new ArrayList<>();
        Set<Object> keys = ppt.keySet();

        for (Object key : keys) {
            if(key.toString().contains("password")){
                int a=key.toString().indexOf(".");
                String firstName = key.toString().substring(0,a);
                firstList.add(firstName);
            }
        }

        Properties properties = new Properties();

        for(String firstName:firstList){
            String userProperty = firstName + ".username";
            String passwordProperty = firstName + ".password";
            String sidProperty = firstName + ".sid";

            String user = ppt.getProperty(userProperty);
            String sid = ppt.getProperty(sidProperty);
            String passWd = ppt.getProperty(passwordProperty);

            if(user == null || "".equals(user)){
                continue;
            }
            if(sid == null || "".equals(sid)){
                continue;
            }
//            if(passWd == null || "".equals(passWd)){
//                //从数据库中获取
//                passWd = PasswordMgr.getPassword(sid.toUpperCase(), user.toUpperCase(), 0);
//            }else{
//                //解密
//                String str3 = ConfigTool.getKey();
//                String str4 = CryptTool.decrypt("", str3);
//                passWd = CryptTool.decrypt(str4, passWd);
//            }
            properties.setProperty(passwordProperty,passWd);
        }
        return properties;
    }





}
