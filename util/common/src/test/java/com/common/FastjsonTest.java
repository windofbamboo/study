package com.common;

import com.common.info.InfoTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;

import java.util.*;

/**
 * fastjson使用样例
 */
public class FastjsonTest {

    /**
     * 序列化 反序列化
     */
    @Test
    public void objectToJSON() throws Exception{

        Map<Long, InfoTest> testMap1 = new HashMap<Long, InfoTest>();
        Map<Long, InfoTest> testMap2 = new HashMap<Long, InfoTest>();

        java.util.Date date = new java.util.Date();
        java.sql.Date dateTime = new java.sql.Date(date.getTime());

        InfoTest infoTest1 = new InfoTest(101L,"abc1",dateTime);
        InfoTest infoTest2 = new InfoTest(102L,"abc2",dateTime);
        InfoTest infoTest3 = new InfoTest(103L,"abc3",dateTime);

        testMap1.put(infoTest1.getId(),infoTest1);
        testMap1.put(infoTest2.getId(),infoTest2);
        testMap1.put(infoTest3.getId(),infoTest3);

        InfoTest infoTest4 = new InfoTest(201L,"101",dateTime);
        InfoTest infoTest5 = new InfoTest(202L,"102",dateTime);
        InfoTest infoTest6 = new InfoTest(203L,"103",dateTime);

        testMap2.put(infoTest4.getId(),infoTest4);
        testMap2.put(infoTest5.getId(),infoTest5);
        testMap2.put(infoTest6.getId(),infoTest6);

        List<Map<Long, InfoTest>> testList = new ArrayList<Map<Long, InfoTest>>();
        testList.add(testMap1);
        testList.add(testMap2);

        // javabean to json 序列化
        String objectJson1 = JSON.toJSONString(infoTest1);
        System.out.println(objectJson1);
        String objectJson2 = JSON.toJSONStringWithDateFormat(infoTest1,"yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(objectJson2);
        // json to javabean 反序列化
        InfoTest parseObject1 = JSON.parseObject(objectJson1, InfoTest.class);
        System.out.println(parseObject1);
        InfoTest parseObject2 = JSON.parseObject(objectJson2, InfoTest.class);
        System.out.println(parseObject2);

        // map to json 序列化
        String mapJson1 = JSON.toJSONStringWithDateFormat(testMap1,"yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(mapJson1);
        // json to map 反序列化
        Map<Long, InfoTest> parseMap = JSON.parseObject(mapJson1,new TypeReference<Map<Long, InfoTest> >(){} );
        System.out.println(parseMap);

        //List<Map> to JSON
        String listJson1 = JSON.toJSONStringWithDateFormat(testList,"yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(listJson1);

        List<Map<Long, InfoTest>> parseList = JSON.parseObject(listJson1,new TypeReference<List<Map<Long, InfoTest>> >(){} );
        System.out.println(parseList);
    }




}
