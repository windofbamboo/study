import backtype.storm.Config;
import com.ai.iot.bill.common.util.JStormUtil;
import com.ai.iot.bill.topo.BillTopology;
import org.junit.Test;

import java.util.LinkedHashMap;


public class TopoTest extends BillTopology {

  @Test
  public void getConfig() {

    String STORM_CONFIG = "acct.yaml";
    Config acctConfig = JStormUtil.getConfig(STORM_CONFIG);

    LinkedHashMap spoutParallelism = (LinkedHashMap)acctConfig.get("topology.spout.parallelism");

    int acctSpoutParallel    = (Integer) spoutParallelism.get("AcctSpout");
    int billSpoutParallel    = (Integer) spoutParallelism.get("BillSpout");
    int deviceSpoutParallel    = (Integer) spoutParallelism.get("DeviceSpout");
    int controlSpoutParallel    = (Integer) spoutParallelism.get("ControlSpout");

    LinkedHashMap boltParallelism = (LinkedHashMap)acctConfig.get("topology.bolt.parallelism");
    int acctBoltParallel    = (Integer) boltParallelism.get("AcctBolt");
    int billBoltParallel    = (Integer) boltParallelism.get("BillBolt");
    int deviceBoltParallel  = (Integer) boltParallelism.get("DeviceBolt");
    int controlBoltParallel = (Integer) boltParallelism.get("ControlBolt");

    System.out.println(spoutParallelism);
    System.out.println(boltParallelism);
  }
}
