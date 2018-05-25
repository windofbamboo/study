import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.define.CommonEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClearKafka {

    private static final Logger logger = LoggerFactory.getLogger(ClearKafka.class);

    public static void defaultClear() {

        Map<String, String> props = new HashMap<String, String>();
        props.put("group.id", "iot");
        props.put("max.poll.records", "20000");
        props.put("auto.offset.reset", "earliest");
        KafkaMq kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_ONLY,props);

        clearTopic(kafkaMq,CommonEnum.TOPIC_ACCT_TASK);
        clearTopic(kafkaMq,CommonEnum.TOPIC_ACCTID);
        kafkaMq.disconnect();
    }

    public static void Clear(String topic) {

        Map<String, String> props = new HashMap<String, String>();
        props.put("group.id", "iot");
        props.put("max.poll.records", "20000");
        props.put("auto.offset.reset", "earliest");
        KafkaMq kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_ONLY,props);

        clearTopic(kafkaMq,topic);
        kafkaMq.disconnect();
    }


    private static void clearTopic(KafkaMq kafkaMq,String topic) {
        int partitions = kafkaMq.getPatitionSize(topic);
        for(int a=0;a<partitions;a++){
            kafkaMq.setPatition(topic,a);
            logger.info("topic :"+ topic + " , partition : " + a);
            System.out.println("topic :"+ topic + " , partition : " + a);
            int b=1;
            while(b>0){
                b=getKafkaData(kafkaMq);
            }
        }
    }

    private static int getKafkaData(KafkaMq kafkaMq){
        List<byte[]> list = kafkaMq.recvMsgBytes(1000);
        if (ListUtil.isNotEmpty(list)) {
            logger.info("size : " + list.size());
            System.out.println("size : " + list.size());
        }
        kafkaMq.commit();
        try {
            Thread.sleep(3);
        } catch (InterruptedException e) {
            logger.error("err:{} ",e);
            e.printStackTrace();
        }
        return list.size();
    }

}
