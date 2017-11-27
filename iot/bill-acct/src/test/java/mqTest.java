import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.entity.DealAcct;
import com.google.gson.Gson;
import org.junit.Test;

/**
 * Created by geyunfeng on 2017/9/20.
 */
public class mqTest {

  @Test
  public void setDealAcct() {

    KafkaMq acctMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_AND_WRITE);
    acctMq.setTopic(CommonEnum.TOPIC_ACCTID);

    Gson gson = new Gson();

//    for(int a=1;a<3;a++){
//      DealAcct dealAcct = new DealAcct(9,3000000001000111L);
//      byte[] data = gson.toJson(dealAcct).getBytes();
//      int re = acctMq.sendMsgBytes(CommonEnum.TOPIC_ACCTID, data);
//      System.out.println("a : "+ a + " ,result: " + re);
//    }

    acctMq.resetBatch();
    for(int a=1;a<2;a++){
      DealAcct dealAcct = new DealAcct(9,3000000001000111L,0);
      byte[] data = gson.toJson(dealAcct).getBytes();
      acctMq.addMsgBytes(CommonEnum.TOPIC_ACCTID, data);
    }
    acctMq.sendBatch();

//    List<byte[]> dataList = acctMq.recvMsgBytes(3000);
//    if(dataList != null && !dataList.isEmpty()){
//      for (byte[] dataNew : dataList) {
//        DealAcct dealAcct2 = gson.fromJson(new String(dataNew), DealAcct.class);
//        System.out.println(dealAcct2);
//      }
//    }
    acctMq.disconnect();

  }


}
