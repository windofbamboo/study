package com.ai.iot.bill.common.db;

import com.ai.iot.bill.common.util.SqlUtil;

import java.util.ArrayList;
import java.util.List;

/**sql配置文件对象
 * Created by geyunfeng on 2017/7/22.
 */
public class XmlSql {

    private String id;          //别名
    private String type;        // 0:insert 1:update 2:delete 3:select
    private String sqlStr;      //解析前的sql
    private String resultType;  //查询返回类型
    private StringBuilder parseSql = new StringBuilder();    //解析后的sql
    private List<String> variables = new ArrayList<>();//变量列表

    public XmlSql() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmlSql xmlSql = (XmlSql) o;

        return id != null ? id.equals(xmlSql.id) : xmlSql.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "XmlSql{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", sqlStr='" + sqlStr + '\'' +
                ", resultType='" + resultType + '\'' +
                ", parseSql='" + parseSql + '\'' +
                ", variables=" + variables +
                '}';
    }

    public void parse(){
        SqlUtil.parseSqlStr(sqlStr,variables,parseSql);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSqlStr() {
        return sqlStr;
    }

    public void setSqlStr(String sqlStr) {
        this.sqlStr = sqlStr;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public StringBuilder getParseSql() {
        return parseSql;
    }

    public List<String> getVariables() {
        return variables;
    }

}
