package com.ai.iot.bill.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**sql转换工具类
 * Created by geyunfeng on 2017/7/22.
 */
public class SqlUtil {

    private static final Logger logger = LoggerFactory.getLogger(SqlUtil.class);

    /**
     * 解析出sql语句中的变量(以#{}包含的部分)
     */
    public static boolean parseSqlStr(String sqlStr,List<String> variables,StringBuilder sql){

        int a=sqlStr.indexOf("#");
        if(a==-1){
            sql.append(sqlStr);
            return true;
        }

        char[] sqlChar=sqlStr.toCharArray();
        char[] sqlCharNew=new char[sqlChar.length];

        int j=0,st=0;
        boolean isVariable=false;
        for(int i=0;i<sqlChar.length;i++){
            /*if((sqlChar[i]=='{'||sqlChar[i]=='}')&&!isVariable){
                System.out.println("第"+i+"位分隔符错误 err!");
                return false;
            }*/
            if(sqlChar[i]=='#'){
                if(sqlChar[i+1]!='{'){
                    System.out.println("第"+i+"位分隔符错误 err!");
                    return false;
                }
                st=i+2;
                sqlCharNew[j]='?';
                j++;
                isVariable=true;
                continue;
            }
            if(sqlChar[i]=='}'&&isVariable){
                variables.add(sqlStr.substring(st,i));
                isVariable=false;
                continue;
            }

            if(!isVariable){
                sqlCharNew[j]=sqlChar[i];
                j++;
            }
        }
        sql.append(sqlCharNew,0,j);
        return true;
    }

    /**
     * 生成批量操作的param 对象，用于QueryRunner 的batch操作
     */
    public static <T> Object[][] getParams(List<String> variables, List<T> tList) throws Exception {

        if(variables==null||variables.isEmpty()){
            logger.error("没有变量");
            return null;
        }

        Object[][] params = new Object[tList.size()][variables.size()];
        Map<String,Field> fieldMap=new HashMap<>();

        for(int a=0;a<tList.size();a++){
            T tRecord=tList.get(a);
            if(fieldMap.isEmpty()){
                Field[] fields=tRecord.getClass().getDeclaredFields();
                for (Field field : fields) {
                    fieldMap.put(field.getName(), field);
                }
            }

            for(int i=0;i<variables.size();i++){
                String name = variables.get(i); // 获取属性的名字
                String type = fieldMap.get(name).getGenericType().toString();
                try {
                    Method m ;
                    if("boolean".equals(type)||"class java.lang.Boolean".equals(type))    {
                        m = tRecord.getClass().getMethod(name);
                    }else{
                        name = name.substring(0,1).toUpperCase() + name.substring(1);
                        m = tRecord.getClass().getMethod("get" + name);
                    }

                    switch (type){
                        case "byte":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "short":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "int":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "long":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "float":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "double":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "boolean":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "char":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "class java.lang.String":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "class java.lang.Integer":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "class java.lang.Long":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "class java.lang.Boolean":
                            params[a][i]= m.invoke(tRecord);
                            break;
                        case "class java.sql.Date":
                            java.sql.Date time =  (java.sql.Date) m.invoke(tRecord);
                            params[a][i]= new Timestamp(time.getTime());
                            break;
                        default:
                            break;
                    }
                } catch (NoSuchMethodException e) {
                    logger.error("找不到对应的方法");
                    throw e;
                } catch (IllegalAccessException e) {
                    logger.error("没有权限");
                    throw e;
                } catch (InvocationTargetException e) {
                    logger.error("未被捕获的异常");
                    throw e;
                }
            }
        }
        return params;
    }

    /**
     * 用于替换sql中的属性值(以${} 包裹的地方)，生成最终使用的sql语句
     */
    public static boolean replaceSqlProperties(Map<String,String> props,StringBuilder sql){

        int a=sql.indexOf("$");
        if(a==-1){
            return true;
        }

        StringBuilder newSql=new StringBuilder();
        int st=0;
        boolean isVariable=false;
        for(int i=0;i<sql.length();i++){
            if((sql.charAt(i)=='{'||sql.charAt(i)=='}')&&!isVariable){
                logger.error("第"+i+"位分隔符错误 err!");
                return false;
            }
            if(sql.charAt(i)=='$'){
                if(sql.charAt(i+1)!='{'){
                    logger.error("第"+i+"位分隔符错误 err!");
                    return false;
                }
                st=i+2;
                isVariable=true;
                continue;
            }
            if(sql.charAt(i)=='}'&&isVariable){
                String prop=sql.substring(st,i);
                if(props.containsKey(prop)){
                    newSql.append(props.get(prop));
                }
                isVariable=false;
                continue;
            }

            if(!isVariable){
                newSql.append(sql.charAt(i));
            }
        }
        sql.setLength(0);
        sql.append(newSql);
        return true;
    }

}
