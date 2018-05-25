package com.common;

import com.common.info.InfoTest;
import com.common.info.InfoTestDao;
import org.apache.ibatis.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Mybatis使用样例
 */
public class MybatisTest {

    @Test
    public void testSql() throws Exception{

        long id1=100011L,id2=100012L,id3=100013L,id4=100014L;
        InfoTest infoTest1 = new InfoTest(id1,"abc");
        InfoTest infoTest2 = new InfoTest(id2,"abc");
        InfoTest infoTest3 = new InfoTest(id3,"abc");
        InfoTest infoTest4 = new InfoTest(id4,"abc");

        List<InfoTest> infoTestList = new ArrayList<>();
        infoTestList.add(infoTest1);
        infoTestList.add(infoTest2);
        infoTestList.add(infoTest3);
        infoTestList.add(infoTest4);

        InfoTestDao.deleteInfoTestList(infoTestList);

        InfoTestDao.insertInfoTestList(infoTestList);

        infoTest1.setPropertyValue("jdslk1");
        infoTest2.setPropertyValue("jdslk2");
        infoTest3.setPropertyValue("jdslk3");
        infoTest4.setPropertyValue("jdslk4");
        InfoTestDao.updateInfoTestList(infoTestList);

    }

    @Test
    public void resSetProperties(){

        Properties ppt=null;
        try {
            String fileName="test-jdbc.properties";
            ppt= Resources.getResourceAsProperties(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ppt==null){
            return;
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
            if(passWd == null || "".equals(passWd)){
                //从数据库中获取
            }else{
                //解密
            }
        }


    }

}
