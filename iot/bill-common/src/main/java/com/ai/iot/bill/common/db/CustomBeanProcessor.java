package com.ai.iot.bill.common.db;

import org.apache.commons.dbutils.BeanProcessor;
import shade.storm.org.apache.commons.lang.StringUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

/**数据库查询,驼峰命名法 替换字段名
 * Created by geyunfeng on 2017/7/22.
 */
public class CustomBeanProcessor extends BeanProcessor {

    @Override
    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd,
                                           PropertyDescriptor[] props) throws SQLException {
        int cols = rsmd.getColumnCount();
        int columnToProperty[] = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(col);
            }
            columnName = colNameConvent(columnName); // 在这里进行数据库表columnName的特殊处理
            for (int i = 0; i < props.length; i++) {

                if (columnName.equalsIgnoreCase(props[i].getName())) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }
        return columnToProperty;
    }

    /**
     * 数据库列名重新约定
     */
    private String colNameConvent(String columnName) {
        String[] strs = columnName.split("_");
        String conventName = "";
        for (String str : strs) {
            conventName += StringUtils.capitalize(str);
        }
        StringUtils.uncapitalize(conventName);
        return conventName;
    }
}
