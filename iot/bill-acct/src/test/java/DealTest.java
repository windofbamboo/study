import com.ai.iot.bill.dealproc.AcctInfoDeal;
import com.ai.iot.bill.dealproc.BillDeal;
import com.ai.iot.bill.dealproc.DeviceDeal;
import com.ai.iot.bill.dealproc.SeqMgr;
import com.ai.iot.bill.dealproc.container.AcctInfoContainer;
import com.ai.iot.bill.dealproc.container.DeviceInfoContainer;
import com.ai.iot.bill.entity.DealAcct;
import com.google.gson.Gson;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyunfeng on 2017/8/7.
 */
public class DealTest {


    @Test
    public void AcctDealTest() throws DocumentException, IOException {

        long dealId=2017112200000050L;
        long acctId=1380000000000000L;

        AcctInfoContainer acctInfoContainer = AcctInfoDeal.acctDealFirst(dealId,acctId);
        List<DeviceInfoContainer> deviceInfoContainerList = AcctInfoDeal.acctDealSecond(acctInfoContainer);

        Gson gson =  new Gson();
        for(DeviceInfoContainer rec : deviceInfoContainerList){
            byte[] data = gson.toJson(rec).getBytes();

            DeviceInfoContainer record = gson.fromJson(new String(data),DeviceInfoContainer.class);
            System.out.println(record);
        }
    }

    @Test
    public void DeviceDealTest() throws DocumentException, IOException {

        long dealId=20171123000000L;
        long acctId=1380020000000000L;
        long deviceId=1180020000000020L;
        AcctInfoContainer acctInfoContainer = AcctInfoDeal.acctDealFirst(dealId,acctId);
        List<DeviceInfoContainer> deviceInfoContainerList = AcctInfoDeal.acctDealSecond(acctInfoContainer);

        if(deviceInfoContainerList!=null && !deviceInfoContainerList.isEmpty()){
            for(DeviceInfoContainer deviceInfoContainer:deviceInfoContainerList){
                if(deviceInfoContainer.getDeviceBean()!=null && deviceInfoContainer.getDeviceBean().getDeviceId()==deviceId){
                    DeviceDeal.deal(deviceInfoContainer);
                }
            }
        }

    }

    @Test
    public void BillDealTest() throws DocumentException, IOException {

        long dealId=2017112400000069L;
        long acctId=1310020000000000L;
        long seqId=2017112400047394L;
        DealAcct dealAcct = new DealAcct(dealId,acctId,seqId);
        BillDeal.deal(dealAcct);
    }

    @Test
    public void mathTest() {

        double tValue = (double)300 * (double)29 / (double)36;
        long b = (long) Math.ceil(tValue);
        System.out.println(b);
        System.out.println(Math.floorMod(3470327423L,10000));

    }



}
