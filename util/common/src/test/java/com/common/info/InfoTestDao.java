package com.common.info;

import com.common.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class InfoTestDao {

    public static InfoTest getInfoTest(long id) throws Exception {

        SqlSession session = MybatisUtil.getSqlSession("roam");
        InfoTest infoTest = session.selectOne("testSqlMapper.getInfoTest", id);
        session.close();
        return infoTest;
    }

    public static List<InfoTest> getInfoTestList(long id) throws Exception {

        SqlSession session = MybatisUtil.getSqlSession("roam");
        List<InfoTest> infoTestList = session.selectList("testSqlMapper.getInfoTest", id);
        session.close();
        return infoTestList;
    }


    public static void insertInfoTest(InfoTest infoTest) throws Exception{
        SqlSession session = MybatisUtil.getSqlSession("roam");
        session.insert("testSqlMapper.insertInfoTest",infoTest);
        session.commit();
        session.close();
    }

    public static void insertInfoTestList(List<InfoTest> infoTestList) {
        SqlSession session = MybatisUtil.getSqlSession("roam");
        if(session == null){
            return;
        }
        int size = infoTestList.size();
        int commitNum = 10000;
        try {
            for (int i = 1; i <= infoTestList.size(); i++) {
                session.insert("testSqlMapper.insertInfoTest", infoTestList.get(i-1));
                if (i % commitNum == 0 || i == size ) {
                    session.commit();
                }
            }
        }catch (Exception e){
            session.rollback();
        }finally {
            session.close();
        }

    }

    public static void updateInfoTest(InfoTest infoTest)throws Exception{
        SqlSession session = MybatisUtil.getSqlSession("roam");
        session.update("testSqlMapper.updateInfoTest",infoTest);
        session.commit();
        session.close();
    }

    public static void updateInfoTestList(List<InfoTest> infoTestList){

        SqlSession session = MybatisUtil.getSqlSession("roam");
        if(session == null){
            return;
        }
        int size = infoTestList.size();
        int commitNum = 10000;
        try {
            for (int i = 1; i <= infoTestList.size(); i++) {
                session.update("testSqlMapper.updateInfoTest", infoTestList.get(i-1));
                if (i % commitNum == 0 || i == size ) {
                    session.commit();
                }
            }
        }catch (Exception e){
            session.rollback();
        }finally {
            session.close();
        }

    }


    public static void deleteInfoTest(long id)throws Exception{
        SqlSession session = MybatisUtil.getSqlSession("roam");
        session.delete("testSqlMapper.deleteInfoTest",id);
        session.commit();
        session.close();
    }

    public static void deleteInfoTestList(List<InfoTest> infoTestList){

        SqlSession session = MybatisUtil.getSqlSession("roam");
        if(session == null){
            return;
        }
        int size = infoTestList.size();
        int commitNum = 10000;
        try {
            for (int i = 1; i <= infoTestList.size(); i++) {
                session.delete("testSqlMapper.deleteInfoTest", infoTestList.get(i-1));
                if (i % commitNum == 0 || i == size ) {
                    session.commit();
                }
            }
        }catch (Exception e){
            session.rollback();
        }finally {
            session.close();
        }

    }

}
