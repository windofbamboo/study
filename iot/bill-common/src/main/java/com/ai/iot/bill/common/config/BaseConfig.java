package com.ai.iot.bill.common.config;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.param.ParamBean;
import com.ai.iot.bill.common.util.CheckNull;
import com.alibaba.jstorm.utils.LoadConf;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BaseConfig {

    public static class ConfigTypeSet {
        boolean withFileConfig = true;
        boolean withDbConfig = true;
        boolean withManagerConfig = true;

        public boolean isWithFileConfig() {
            return withFileConfig;
        }

        public boolean isWithDbConfig() {
            return withDbConfig;
        }

        public boolean isWithManagerConfig() {
            return withManagerConfig;
        }

        public void setWithFileConfig(boolean withFileConfig) {
            this.withFileConfig = withFileConfig;
        }

        public void setWithDbConfig(boolean withDbConfig) {
            this.withDbConfig = withDbConfig;
        }

        public void setWithManagerConfig(boolean withManagerConfig) {
            this.withManagerConfig = withManagerConfig;
        }
    }

    private ConfigTypeSet configTypeSet = new ConfigTypeSet();
    private static String paramDbRouteId;
    private Map<String, Object> fileProperties;
    private Map<String, String> dbProperties;
    private Map<String, ParamBean> dbParamBeans;
    private Map<String, String> managerProperties;
    private String yamlFileName;
    private ParamManagerAdapterInterface paramManagerAdapter;
    public Map<String, Object> getFileProperties() {
        return fileProperties;
    }

    public void setFileProperties(Map<String, Object> fileProperties) {
        this.fileProperties = fileProperties;
    }

    public Map<String, String> getDbProperties() {
        return dbProperties;
    }

    public void setDbProperties(Map<String, String> dbProperties) {
        this.dbProperties = dbProperties;
    }

    public Map<String, ParamBean> getDbParamBeans() {
        return dbParamBeans;
    }

    public void setDbParamBeans(Map<String, ParamBean> dbParamBeans) {
        this.dbParamBeans = dbParamBeans;
    }

    public Map<String, String> getManagerProperties() {
        return managerProperties;
    }

    public void setManagerProperties(Map<String, String> managerProperties) {
        this.managerProperties = managerProperties;
    }

    public BaseConfig(ConfigTypeSet configTypeSet) {
        this.configTypeSet = configTypeSet;
    }

    @SuppressWarnings("unchecked")
    public void init(String configFile, String paramMysqlCode, ParamManagerAdapterInterface paramManagerAdapter) throws ConfigException {
        if (configTypeSet.isWithFileConfig()) {
            if (null == configFile) {
                throw new ConfigException(ConfigException.ExceptionENUM.CONFIG_FILE_NOT_EXIST);
            } else if (!configFile.endsWith(".yaml")) {
                throw new ConfigException(ConfigException.ExceptionENUM.CONFIG_FILE_IS_NOT_YAML);
            }
            fileProperties = new HashMap<>();
            fileProperties = LoadConf.LoadYaml(configFile);
            yamlFileName = configFile;
        }

        if (configTypeSet.isWithDbConfig()) {
            if (null == paramMysqlCode) {
                throw new ConfigException(ConfigException.ExceptionENUM.CONFIG_DBNAME_IS_NULL);
            }

            BaseConfig.paramDbRouteId = paramMysqlCode;
            dbProperties = new HashMap<>();
            dbParamBeans = new HashMap<>();
        }

        if (configTypeSet.isWithManagerConfig()) {
            if (null == paramManagerAdapter) {
                throw new ConfigException(ConfigException.ExceptionENUM.CONFIG_MANAGER_IS_NULL);
            }
            this.paramManagerAdapter = paramManagerAdapter;
            managerProperties = new HashMap<>();
        }
    }

    public String getYamlFileName() {
        return yamlFileName;
    }

    public void setYamlFileName(String yamlFileName) {
        this.yamlFileName = yamlFileName;
    }

    public ConfigTypeSet getConfigTypeSet() {
        return configTypeSet;
    }

    public void setConfigTypeSet(ConfigTypeSet configTypeSet) {
        this.configTypeSet = configTypeSet;
    }

    public ParamManagerAdapterInterface getParamManagerAdapter() {
        return paramManagerAdapter;
    }

    /**
     *  根据话单的GUID 获取缓存的话单信息
     *  @param paramName  参数名
     *  @param paramOrigin 参数来源：1：yaml文件 2：mysql参数库表 3：parammanager参数管理器
     *  @return  话单信息
     *  @since  1.2
     */
    public Object getParam(String paramName, int paramOrigin) {
        Object paramValue = null;
        switch (paramOrigin) {
            case 1:
                if (CheckNull.isNull(fileProperties)) {
                    break;
                }

                paramValue = fileProperties.get(paramName);
                break;
            case 2:
                if (CheckNull.isNull(dbProperties) && CheckNull.isNull(dbParamBeans)) {
                    break;
                }

                if (!CheckNull.isNull(dbProperties)) {
                    paramValue = dbProperties.get(paramName);
                    if (CheckNull.isNull(paramValue)) {
                        if (!CheckNull.isNull(dbParamBeans)) {
                            paramValue = dbParamBeans.get(paramName);
                        }
                    }
                }
                break;
            case 3:
                if (CheckNull.isNull(paramManagerAdapter)) {
                    break;
                }
                paramValue = paramManagerAdapter.getParam(paramName);
                break;
            default:
        }
        return paramValue;
    }

    private static String generateParamSql(ParamBean paramBean) {
        String sqlstr = null;
        StringBuffer sqlSb = new StringBuffer("select param_value as paramValue,param_value2 as paramValue2,param_value3 as paramValue3 from TD_B_PARAM " +
                "where param_type=? and param_name=? " );
        if (CheckNull.isNull(paramBean)) {
        } else if (!CheckNull.isNull(paramBean.getParamValue())) {
            sqlSb.append("and param_value=?");
        } else if (!CheckNull.isNull(paramBean.getParamValue2())) {
            sqlSb.append("and param_value2=?");
        } else if (!CheckNull.isNull(paramBean.getParamValue3())) {
            sqlSb.append("and param_value3=?");
        }
        sqlstr = sqlSb.append(";").toString();
        return sqlstr;
    }

    @SuppressWarnings("unused")
    private static ParamBean getParamBeanFromDb(String type, String name, ParamBean paramBean) {
        String sqlstr = generateParamSql(paramBean);
        try {
            DataSource ds= DataSourceMgr.getDataSource(BaseConfig.paramDbRouteId);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanHandler<>(ParamBean.class),type,name);
        }catch (Exception e){
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static List<ParamBean> getParamBeansFromDb(String type, String name, ParamBean paramBean) {
        String sqlstr = generateParamSql(paramBean);
        try {
            DataSource ds= DataSourceMgr.getDataSource(paramDbRouteId);

            QueryRunner qr = new QueryRunner(ds);

            return qr.query(sqlstr, new BeanListHandler<>(ParamBean.class));
        }catch (Exception e){
            return null;
        }
    }
}
