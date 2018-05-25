import com.ai.iot.bill.define.CommonEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

    /**
     * 参数格式
     * insert prov/acctId id
     * clear topic
     */
    public static void main(String args[]) {

        logger.info("start !");
        System.out.println("start !");
        if(!checkArgs(args)){
            logger.error("args err!");
            helpPrint();
            return;
        }
        String type = args[0];
        if("clear".equals(type)){
            if(args.length == 1){
                ClearKafka.defaultClear();
            }else{
                String topic =args[1];
                ClearKafka.Clear(topic);
            }
        }else if("insert".equals(type)){
            String idType =args[1];
            String idStr = args[2];
            String[] idList = idStr.split(",");
            if("prov".equals(idType) ){
                ArrayList<String> provList = new ArrayList<String>();
                Collections.addAll(provList, idList);
                if(!provList.isEmpty()){
                    WriteKafka.sendProvTask(provList);
                }
            }else if("acctId".equals(idType)){
                ArrayList<Long> acctList = new ArrayList<Long>();
                for(String id:idList){
                    long acctId = Long.parseLong(id);
                    acctList.add(acctId);
                }
                if(acctList.isEmpty()){
                    WriteKafka.sendAcctTask(acctList);
                }
            }
        }else if("reload".equals(type)){
            String dealStr =args[1];
            long dealId = Long.parseLong(dealStr);
            List<Long> acctList = WriteKafka.getAcctList(dealId);
            System.out.println("acctList:"+acctList.size());
            if(acctList.isEmpty()){
                WriteKafka.sendAcctTask(acctList);
            }
        }
        logger.info("end !");
        System.out.println("end !");
    }


    private static boolean checkArgs(String args[]){

        if(args.length<1 || args.length>3){
            return false;
        }
        String type = args[0];
        if("clear".equals(type)){
            if(args.length > 2){
                return false;
            }
            if(args.length == 2){
                String topic = args[1];
                if(!CommonEnum.TOPIC_ACCT_TASK.equals(topic) && !CommonEnum.TOPIC_ACCTID.equals(topic)){
                    return false;
                }
            }
        }else if("insert".equals(type)){
            if(args.length != 3){
                return false;
            }
            String idType =args[1];
            if(!"prov".equals(idType) && !"acctId".equals(idType)){
                return false;
            }
        }else if("reload".equals(type)){
            if(args.length != 2){
                return false;
            }
        }else{
            return false;
        }
        return true;
    }


    private static void helpPrint(){

        System.out.println("args list:");
        System.out.println("\tinsert ID_TYPE[prov|acctId] ID[str1,str2,str3,str4]");
        System.out.println("\tclear [topic] ");
        System.out.println("\treload [dealId] ");
    }







}
