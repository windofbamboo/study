package com.ai.iot.bill.common.db;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**mapper配置文件读取类
 * Created by geyunfeng on 2017/7/22.
 */
public class XmlMgr {

    private static final Logger logger = LoggerFactory.getLogger(XmlMgr.class);
    private static Map<String,Map<String,XmlSql>> nameSpaceMap=new ConcurrentHashMap<>();

    private static Lock tLock = new ReentrantLock();

    public static XmlSql getXmlSql(String id){

        int se=id.lastIndexOf('.');
        String nameSpace=id.substring(0,se);
        String sqlId=id.substring(se+1);

        if(init(nameSpace)){
            Map<String,XmlSql> sqlMap=nameSpaceMap.get(nameSpace);
            if(sqlMap.containsKey(sqlId) ){
                return sqlMap.get(sqlId);
            }
            logger.error("sqlid not found !");
            return null;
        }
        logger.error(nameSpace +".xml parse err ! ");
        return null;
    }

    private static boolean init(String nameSpace){

        try {
            tLock.lock();
            if(!initNameSpace(nameSpace)){
                return false;
            }
        }catch (Exception e){
            logger.error("init nameSpace err :{}",e);
            return false;
        }finally {
            tLock.unlock();
        }
        return true;
    }

    private static boolean initNameSpace(String nameSpace){

        if(nameSpaceMap.containsKey(nameSpace)){
            return true;
        }

        String file = nameSpace+".xml";
        try {
            Map<String,XmlSql> sqlMap=readXml(file);
            nameSpaceMap.put(nameSpace,sqlMap);
        } catch (Exception e) {
            logger.error(nameSpace+".xml parse read err :{}",e);
            return false;
        }
        return true;
    }

    private static Map<String,XmlSql> readXml(String file) throws Exception{

        ErrorContext.instance().resource(file);
        InputStream inputStream = Resources.getResourceAsStream(file);

        org.dom4j.Document doc = new SAXReader().read(inputStream);

        Map<String,XmlSql> sqlMap=parseDocument(doc,"select");
        sqlMap.putAll(parseDocument(doc,"insert"));
        sqlMap.putAll(parseDocument(doc,"update"));
        sqlMap.putAll(parseDocument(doc,"delete"));

        return sqlMap;
    }


    /**
     * 从Document中读出sql语句的配置信息
     */
    private static Map<String,XmlSql> parseDocument(Document doc,String nodeName) throws Exception{

        Element root = doc.getRootElement();
        List<?> nodes = root.elements(nodeName);
        Map<String,XmlSql> sqlMap=new ConcurrentHashMap<>();

        for (Object node : nodes) {
            Element elm = (Element) node;

            XmlSql xmlSql = new XmlSql();
            xmlSql.setSqlStr(elm.getTextTrim());
            xmlSql.setType(nodeName);

            for (Iterator<?> itr = elm.attributeIterator(); itr.hasNext(); ) {
                Attribute attr = (Attribute) itr.next();
                switch (attr.getName()) {
                    case "id":
                        xmlSql.setId(attr.getValue().trim());
                        break;
                    case "resultType":
                        xmlSql.setResultType(attr.getValue().trim());
                        break;
                    default:
                        break;
                }
            }
            xmlSql.parse();
            sqlMap.put(xmlSql.getId(), xmlSql);
        }
        return sqlMap;
    }



}
