package com.ai.iot.bill.common.mgr;

import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.google.gson.Gson;

/**
 * Created by geyunfeng on 2017/7/10.
 */
public class KafkaTest {

    public static void main(String[] args) {

        Gson gson = new Gson();
        KafkaMq mq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.WRITE_ONLY);
//        mq.setTopic("TOP_CDRS_RATE_SM");
        mq.setPatition("TOP_CDRS_RATE_SM",1);

        String sendString="1233 dfsefds sfjdifh iofshifpyiufoe  fidohfshoihoi  fdoijoifpsouoei!#*dfe";

        /*mq.sendMsg("TOP_CDRS_RATE_SM",sendString);
        List<String> recvString =mq.recvMsgs(3000);
        System.out.println(recvString);*/

        byte[] data=gson.toJson(sendString).getBytes();
        mq.sendMsgBytes("TOP_CDRS_RATE_SM",data);


//        byte[] tRec =mq.recvMsgByte(3000);
//        System.out.println(tRec);
//        String recvString = gson.fromJson(new String(tRec), String.class);
//        System.out.println(recvString);

        mq.disconnect();
        System.out.println("over!");
    }
}
