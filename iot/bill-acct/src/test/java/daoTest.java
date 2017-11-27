import com.ai.iot.bill.common.db.XmlBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.route.RouteMgr;
import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.dao.ParamDao;
import com.ai.iot.bill.dao.util.BaseDao;
import com.ai.iot.bill.dealproc.SeqMgr;
import com.ai.iot.bill.entity.info.SharePoolBean;
import com.ai.iot.bill.entity.log.DeviceLog;
import com.ai.iot.bill.entity.multibill.AcctBillSum;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class daoTest {


    @Test
    public void daoTest2(){

        long acctId=1310010000000000L;
        long dealId=2017103000000062L;
        int dealNum = LogDao.getDeviceLogNum(acctId,dealId);
        System.out.println(dealNum==0);
    }

    @Test
    public void getBillId(){

        SeqMgr.getSeqId("BILL_ID");
        List<Long> billList = new ArrayList<>();
        long s =System.currentTimeMillis();
        for(int a=0;a<10000;a++){
            long billId = SeqMgr.getSeqId("BILL_ID");
            billList.add(billId);
        }
        long e =System.currentTimeMillis();
        System.out.println("all time is : " + (e-s));
        billList.forEach(System.out::println);
    }


}
