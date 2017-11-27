package mdb;

import com.ai.iot.bill.common.util.StringUtil;
import com.ai.iot.bill.dao.MdbAddDao;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by geyunfeng on 2017/9/30.
 */
public class MdbDataTest {

  private Map<String,MdbXml> updateMap=new ConcurrentHashMap<>();

  private Map<String,MdbXml> readXml() throws Exception{

//    File f = new File(this.getClass().getResource("").getPath());
//    System.out.println(f.getName());

    String file = "mdbdata.xml";

    ErrorContext.instance().resource(file);

    InputStream inputStream = Resources.getResourceAsStream(file);

    org.dom4j.Document doc = new SAXReader().read(inputStream);

    return parseDocument(doc,"insert");
  }

  private Map<String,MdbXml> parseDocument(Document doc, String nodeName) throws Exception{

    Element root = doc.getRootElement();
    List<?> nodes = root.elements(nodeName);
    Map<String,MdbXml> dataMap=new ConcurrentHashMap<>();

    for (Object node : nodes) {
      Element elm = (Element) node;

      MdbXml mdbXml = new MdbXml();
      mdbXml.setValues(elm.getTextTrim());

      for (Iterator<?> itr = elm.attributeIterator(); itr.hasNext(); ) {
        Attribute attr = (Attribute) itr.next();
        switch (attr.getName()) {
          case "id":
            mdbXml.setId(attr.getValue().trim());
            break;
          case "fieldValue":
            mdbXml.setFieldValue(attr.getValue().trim());
            break;
          case "valueType":
            mdbXml.setValueType(attr.getValue().trim());
            break;
          default:
            break;
        }
      }
      dataMap.put(mdbXml.getId(), mdbXml);
    }
    return dataMap;
  }

  private void parseValue(final Map<String,String> valueMap,
                          final MdbXml mdbXml,
                          Map<MdbKey,MdbValue> mdbMap ) {

    if(valueMap==null || valueMap.isEmpty()){
      return;
    }
    if(mdbXml==null){
      return;
    }
    Class<?> classType= null;
    try {
      classType = Class.forName(mdbXml.getValueType());
    } catch (ClassNotFoundException e) {
      System.out.println("get class err");
      return;
    }

    MdbKey key = new MdbKey();
    if(valueMap.containsKey("acctId")){
      key.setAcctId( StringUtil.toLong(valueMap.get("acctId")) );
    }
    if(valueMap.containsKey("deviceId")){
      key.setDeviceId( StringUtil.toLong(valueMap.get("deviceId")) );
    }
    if(valueMap.containsKey("cycleId")){
      key.setCycleId( StringUtil.toInt(valueMap.get("cycleId")) );
    }
    if(valueMap.containsKey("poolId")){
      key.setPoolId( StringUtil.toLong(valueMap.get("poolId")) );
    }

    MdbValue mdbValue;
    if(mdbMap.containsKey(key)){
      mdbValue = mdbMap.get(key);
    }else {
      mdbValue = new MdbValue();
      mdbMap.put(key,mdbValue);
    }
    String name="get" + classType.getSimpleName() + "List";
    List tlist;
    try {
      Method n = mdbValue.getClass().getMethod(name);
      tlist=(List)n.invoke(mdbValue);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return;
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      return;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return;
    }

    try {
      Object o = classType.newInstance();
      Field[] fields = classType.getDeclaredFields();
      for(int a=0;a<fields.length;a++){
        String fieldType = fields[a].getGenericType().toString();
        String fieldName = fields[a].getName();
        String methodName ="set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
        try {
          Method m = classType.getMethod(methodName,fields[a].getType());

          switch (fieldType){
            case "byte":
              m.invoke(o,  (byte)StringUtil.toInt(valueMap.get(fieldName)));
              break;
            case "short":
              m.invoke(o, (short)StringUtil.toInt(valueMap.get(fieldName)) );
              break;
            case "int":
              m.invoke(o, StringUtil.toInt(valueMap.get(fieldName)) );
              break;
            case "long":
              m.invoke(o, StringUtil.toLong(valueMap.get(fieldName)) );
              break;
            case "float":
              m.invoke(o, (float) StringUtil.toDouble(valueMap.get(fieldName))  );
              break;
            case "double":
              m.invoke(o, StringUtil.toDouble(valueMap.get(fieldName))  );
              break;
            case "boolean":
              m.invoke(o, StringUtil.toBoolean(valueMap.get(fieldName))  );
              break;
            case "char":
              m.invoke(o, valueMap.get(fieldName).charAt(0) );
              break;
            case "class java.lang.String":
              m.invoke(o, valueMap.get(fieldName)   );
              break;
            default:
              break;
          }
        } catch (NoSuchMethodException e) {
          continue;
        }catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }

      tlist.add(o);

    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

  }

  private void updateValue(Map<MdbKey,MdbValue> mdbMap) throws Exception{

    if(mdbMap==null || mdbMap.isEmpty() ){
      return;
    }
    for(MdbKey key : mdbMap.keySet()){
      MdbValue value=mdbMap.get(key);
      if(!value.getMdbBillPoolSum2GprsList().isEmpty()){
          MdbAddDao.setPoolAdd(key.getCycleId(),key.getAcctId(),key.getPoolId(),value.getMdbBillPoolSum2GprsList());
      }

      if(!value.getMdbBillUserSum1GprsList().isEmpty()||
         !value.getMdbBillUserSum1GsmList().isEmpty()||
         !value.getMdbBillUserSum1SmsList().isEmpty()||
         !value.getMdbBillUserSum2GprsList().isEmpty()||
         !value.getMdbBillUserSum2GsmList().isEmpty()||
         !value.getMdbBillUserSum2SmsList().isEmpty()){
        MdbAddDao.setDeviceAdd(key.getCycleId(),key.getAcctId(),key.getDeviceId(),
            value.getMdbBillUserSum1GprsList(),
            value.getMdbBillUserSum2GprsList(),
            value.getMdbBillUserSum1GsmList(),
            value.getMdbBillUserSum2GsmList(),
            value.getMdbBillUserSum1SmsList(),
            value.getMdbBillUserSum2SmsList());
      }

    }

  }



  @Test
  public void setAddValue() throws Exception{

    updateMap=readXml();
    Map<MdbKey,MdbValue> mdbMap = new HashMap<>();

    if(!updateMap.isEmpty()){
      //获取值
      for(String id:updateMap.keySet()){
        MdbXml mdbXml = updateMap.get(id);
        if("".equals(mdbXml.getValues()))
          continue;

        String columns[] = mdbXml.getFieldValue().split(",");
        String rows[] = mdbXml.getValues().split(" ");
        for(int a=0;a< rows.length;a++){
          String columnValues[] = rows[a].split(",");
          if(columnValues.length!=columns.length){
            continue;
          }
          Map<String,String> valueMap=new HashMap<>();
          for(int b=0;b<columnValues.length;b++){
            valueMap.put(columns[b],columnValues[b]);
          }
          parseValue(valueMap,mdbXml,mdbMap);
        }
      }

    }
    //更新值
    updateValue(mdbMap);
  }



}
