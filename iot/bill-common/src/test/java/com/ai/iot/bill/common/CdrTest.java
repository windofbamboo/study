package com.ai.iot.bill.common;


import com.ai.iot.bill.common.cdr.*;
import com.ai.iot.bill.common.util.Const;
import org.junit.Test;
import java.util.Map;

/**
 * Created by zhangrui on 2017/7/7.
 */
@SuppressWarnings("unused")
public class CdrTest {
    @Test
    public void main() {
        //首先创建一个话单转换器，话单格式就是在这时候加载的。
        CdrFormatter cdrFormatter = null;
        try {
            cdrFormatter = new CdrFormatterImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 示例无索引字串
        String cdrstr = "f67003e6-50a8-4655-9cb2-9192ff6bcf55,3,0,1,imsi110,msisdn133301,imei000001,sgsn001,2,600,msc0,msisdn_b131000,20170101,000000,20170102,000000,fileno123";

        Cdr cdr = new Cdr();
        //无索引字串构建话单对象
        try {
            //cdr = Cdr.fromString(cdrstr, Const.SOURCETYPE_POS_PARSE_0);
            if (null!=cdrFormatter) {
                cdr = cdrFormatter.str2Cdr(cdrstr, Const.SOURCETYPE_POS_PARSE_0);
                cdr = cdrFormatter.str2Cdr(cdrstr, Const.PROC_FILTER);
                cdr = cdrFormatter.str2CdrById(cdrstr, 11);
            }
        } catch (CdrException e) {
            e.printStackTrace();
        }

        //话单对象转成无索引字串
        if (null!=cdr) {
            String str = cdr.toString();
        }

        System.out.println("This is test!");

        // 示例索引字串
        cdrstr = "510,f67003e6-50a8-4655-9cb2-9192ff6bcf55,0,3,511,0,1,1,2,imsi110,3,msisdn133301,4,imei000001," +
                "303,sgsn001,300,2,301,600,302,msc0,203,msisdn_b131000,405,20170101,406,000000,407,20170102,408,000000,500,fileno123";
        cdrstr = "100,31,101,3,102,e051b03f-33a4-45da-aaf7-e3dacfbca378,105,460061031060528,106,861064611311519,109,0,111,20171011094107816,112,,115,000054,116,,117,0,120,QB02#PS_R9_V940_China_Unicom#pgwcdr#20170517093005_000006.btf,124,,125,19";
        //索引字串构建话单对象
        try {
            //cdr = Cdr.fromIndexString(cdrstr, Const.PROC_FILTER);
            if (null != cdr) {
                cdr.reset();
                cdr.fromIndexString(cdrstr);
                String hoho = cdr.get(CdrAttri.ATTRI_DEVICE_ID);
                System.out.println("temppause");
            }

            cdr = cdrFormatter.indexStr2Cdr(cdrstr, Const.PROC_FILTER);
        } catch (CdrException e) {
            e.printStackTrace();
        }

        //话单转成索引字串
        String indexCdrStr = cdr.toIndexString();
        try {
//            String indexCdrStr1 = cdr.toIndexString(4);
//            String indexCdrStr2 = cdr.toIndexString("cdrloader");
//            String indexCdrStr3 = cdr.toIndexStringWithCdrformatId(10);
            String indexCdrStr1 = cdrFormatter.cdr2IndexStr(cdr, 4);
            String indexCdrStr2 = cdrFormatter.cdr2IndexStr(cdr, "loadVoice");
            String indexCdrStr3 = cdrFormatter.cdr2IndexStrWithCdrformatId(cdr, 10);
        } catch (CdrException e) {
            e.printStackTrace();
        }

        //获取多行属性
        // 示例索引字串 该字符串中有2个imsi
        cdrstr = "510,f67003e6-50a8-4655-9cb2-9192ff6bcf55,0,3,511,0,1,1,10002,imsi110,20002,imsi110,3,msisdn133301,4,imei000001," +
                "303,sgsn001,300,2,301,600,302,msc0,203,msisdn_b131000,405,20170101,406,000000,407,20170102,408,000000,500,fileno123";

        try {
            //cdr = Cdr.fromIndexString(cdrstr, Const.PROC_FILTER);
            cdr = cdrFormatter.indexStr2Cdr(cdrstr, Const.PROC_FILTER);
        } catch (CdrException e) {
            e.printStackTrace();
        }
        Map<Integer, String> mAttr = cdr.getMultiLineAttr(2);


        //定长字段字串构成话单对象
        //示例字串
        //cdrstr = "122333444455555666666";
        cdrstr = "12233 44  55   666666";
        try {
            //cdr = Cdr.fromUnRegularString(cdrstr, "cdrloader");
            cdr = cdrFormatter.fixStr2Cdr(cdrstr, "loadVoice");
        } catch (CdrException e) {
            e.printStackTrace();
        }

        //话单对象转成定长字段字串
        //String unRegularString = cdr.toUnRegularString();
        String unRegularString = cdrFormatter.cdr2FixStr(cdr, "loadVoice");

        //话单对象序列化
        byte[] bytes = cdrFormatter.toBytes(cdr);

        //反序列化
        Cdr another = cdrFormatter.fromBytes(bytes);

        //测试结束
        System.out.println("test is over.");
    }
}
