import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.define.ErrEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WriteKafka {

    private static final Logger logger = LoggerFactory.getLogger(WriteKafka.class);
    //按省份
    public static void sendProvTask(ArrayList<String> provList) {
        KafkaMq kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_AND_WRITE);
        String msg;
        kafkaMq.resetBatch();

        for (String provinceCode : provList) {
            msg = "{'acctId':'', 'provinceCode':'" + provinceCode + "'}";
            System.out.println(msg);
            kafkaMq.addMsg(CommonEnum.TOPIC_ACCT_TASK, msg);
        }

        List<Integer> rets = kafkaMq.sendBatch();
        kafkaMq.disconnect();
        logger.info("rets : "+rets.toString());
        System.out.println("rets : "+rets.toString());
    }

    //按账户
    public static void sendAcctTask(List<Long> acctList) {
        KafkaMq kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_AND_WRITE);
        String msg;
        kafkaMq.resetBatch();

        for(long acctId: acctList){
            msg = "{'acctId':'"+acctId+"', 'provinceCode':''}";
            System.out.println(msg);
            kafkaMq.addMsg(CommonEnum.TOPIC_ACCT_TASK, msg);
        }
        List<Integer> rets=kafkaMq.sendBatch();
        kafkaMq.disconnect();
        logger.info("rets : "+rets.toString());
        System.out.println("rets : "+rets.toString());
    }

    public static List<Long> getAcctList(long dealId ){

        String qureySql = " select ACCT_ID from TL_B_ACCT where DEAL_ID=? " +
                          " and DEAL_STAGE != " + BillEnum.DealStage.BILL_DEAL_END +" and deal_tag!="+ ErrEnum.DealTag.SUCCESS;

        List<Long> acctList =new ArrayList<Long>();

        Connection conn = DataSourceMgr.getConnection(BaseDefine.CONNCODE_MYCAT_PARAM);
        PreparedStatement prep = null;
        ResultSet rst = null;
        try {
            prep = conn.prepareStatement(qureySql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            prep.setFetchSize(2000);
            prep.setFetchDirection(ResultSet.FETCH_REVERSE);
            prep.setLong(1,dealId);

            rst = prep .executeQuery();
            while (rst.next()) {
                acctList.add(rst.getLong(1));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (rst != null) {
                    rst.close();
                }
                if (prep != null) {
                    prep.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return acctList;
    }




}
