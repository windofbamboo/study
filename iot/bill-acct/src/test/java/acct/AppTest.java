package acct;

import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.dao.AcctValidDao;
import com.ai.iot.bill.dao.util.BaseDao;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.entity.DealAcct;
import com.google.gson.Gson;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

public class AppTest {

  @Test
  public void sendAcctTask() {
    KafkaMq kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.WRITE_ONLY);
    String msg;
    kafkaMq.resetBatch();
    //按省份
//    for(int i=0; i<2; i++){
//      int x = 10%3+1;
//      msg = "{'acctId':'', 'provinceCode':'0"+x+"0'}";
//      System.out.println(msg);
//      kafkaMq.addMsg(CommonEnum.TOPIC_ACCT_TASK, msg);
//    }
    //按账户

    ArrayList<Long> acctList = new ArrayList<>();
    acctList.add(1310010000000000L);
    acctList.add(1310020000000000L);

    acctList.add(1320010000000000L);
    acctList.add(1320020000000000L);
    acctList.add(1320030000000000L);
    acctList.add(1320040000000000L);

    acctList.add(1330010000000000L);
    acctList.add(1330020000000000L);
    acctList.add(1330030000000000L);
    acctList.add(1330040000000000L);
    acctList.add(1330050000000000L);
    acctList.add(1330060000000000L);
    acctList.add(1330070000000000L);
    acctList.add(1330080000000000L);
    acctList.add(1330090000000000L);
    acctList.add(1330100000000000L);
    acctList.add(1330110000000000L);
    acctList.add(1330120000000000L);
    acctList.add(1330130000000000L);
    acctList.add(1330140000000000L);
    acctList.add(1330150000000000L);
    acctList.add(1330160000000000L);
    acctList.add(1330170000000000L);
    acctList.add(1330180000000000L);

    acctList.add(1340010000000000L);
    acctList.add(1340020000000000L);
    acctList.add(1340030000000000L);
    acctList.add(1340040000000000L);
    acctList.add(1340050000000000L);
    acctList.add(1340060000000000L);
    acctList.add(1340070000000000L);
    acctList.add(1340080000000000L);
    acctList.add(1340090000000000L);
    acctList.add(1340100000000000L);
    acctList.add(1340110000000000L);
    acctList.add(1340120000000000L);
    acctList.add(1340130000000000L);
    acctList.add(1340140000000000L);
    acctList.add(1340150000000000L);
    acctList.add(1340160000000000L);
    acctList.add(1340170000000000L);
    acctList.add(1340180000000000L);
    acctList.add(1340190000000000L);
    acctList.add(1340200000000000L);

    acctList.add(1350010000000000L);
    acctList.add(1350020000000000L);
    acctList.add(1350030000000000L);
    acctList.add(1350040000000000L);
    acctList.add(1350050000000000L);
    acctList.add(1350060000000000L);
    acctList.add(1350070000000000L);
    acctList.add(1350080000000000L);
    acctList.add(1350090000000000L);
    acctList.add(1350100000000000L);
    acctList.add(1350110000000000L);
    acctList.add(1350120000000000L);
    acctList.add(1350130000000000L);
    acctList.add(1350140000000000L);

    acctList.add(1360010000000000L);
    acctList.add(1360020000000000L);
    acctList.add(1360030000000000L);
    acctList.add(1360040000000000L);
    acctList.add(1360050000000000L);
    acctList.add(1360060000000000L);
    acctList.add(1360070000000000L);
    acctList.add(1360080000000000L);
    acctList.add(1360090000000000L);
    acctList.add(1360100000000000L);
    acctList.add(1360110000000000L);
    acctList.add(1360120000000000L);
    acctList.add(1360130000000000L);
    acctList.add(1360140000000000L);

    acctList.add(1370010000000000L);
    acctList.add(1370020000000000L);
    acctList.add(1370030000000000L);

    acctList.add(1380000000000000L);
    acctList.add(1380010000000000L);
    acctList.add(1380020000000000L);
    acctList.add(1380030000000000L);
    acctList.add(1380040000000000L);
    acctList.add(1380050000000000L);
    acctList.add(1380060000000000L);

    acctList.add(1390010000000000L);
    acctList.add(1390020000000000L);

//    acctList.clear();
//    acctList.add(1380000000000000L);

    for(int a=0; a<acctList.size(); a++){
      msg = "{'acctId':'"+acctList.get(a)+"', 'provinceCode':''}";
      System.out.println(msg);
      kafkaMq.addMsg(CommonEnum.TOPIC_ACCT_TASK, msg);
    }
    List<Integer> rets=kafkaMq.sendBatch();
    System.out.println("=============rets============= "+rets.toString());
    kafkaMq.disconnect();
  }

  @Test
  public void getAcctTask() {
    List<byte[]> list = null;
    Map<String, String> props = new HashMap<>();
    props.put("group.id", "iot");
    props.put("max.poll.records", "2000");
    props.put("auto.offset.reset", "earliest");
    KafkaMq kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_ONLY, props);

    while (true) {
      kafkaMq.setTopic(CommonEnum.TOPIC_ACCT_TASK);
      System.out.println("================TOPIC_ACCT_TASK==============" );
      getKafkaData(kafkaMq);
      kafkaMq.setTopic(CommonEnum.TOPIC_ACCTID);
      System.out.println("================TOPIC_ACCTID==============" );
      getKafkaData(kafkaMq);
      kafkaMq.setTopic(CommonEnum.TOPIC_DEVICEINFO);
      System.out.println("================TOPIC_DEVICEINFO==============" );
      getKafkaData(kafkaMq);
      kafkaMq.setTopic(CommonEnum.TOPIC_CONTROL);
      System.out.println("================TOPIC_CONTROL==============" );
      getKafkaData(kafkaMq);
      kafkaMq.setTopic(CommonEnum.TOPIC_BILL);
      System.out.println("================TOPIC_BILL==============" );
      getKafkaData(kafkaMq);
    }
  }

  private void getKafkaData(KafkaMq kafkaMq){
    List<byte[]> list = kafkaMq.recvMsgBytes(2000);
    System.out.println(list.isEmpty());
    if (ListUtil.isNotEmpty(list)) {
      System.out.println("================topic==============" + list.size());
      for (byte[] bytes : list) {
        System.out.println(new String(bytes));
      }
    }
    kafkaMq.commit();
    try {
      Thread.sleep(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void insertRedis() {
    Connection conn = BaseDao.getConnection(BaseDefine.CONNCODE_REDIS_PARAM);
    QueryRunner qr = new QueryRunner();
    BaseDao.execsql(qr, conn, "mdbdata.MdbBillUserSum1Gprs");
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test(){
    while(true){
      List<String> acctIdList = AcctValidDao.getAcctListByProvCode("3040000003000111");
      System.out.println(acctIdList.toString());
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void test2(){
    Gson gson = new Gson();
    DealAcct dealAcct = new DealAcct(100, Long.valueOf("010"),0);
    System.out.println(gson.toJson(dealAcct));
  }

  @Test
  public void test3(){
    String[] tables = {
            "bill_pool_sum2_gprs",
            "bill_stat_gprs",
            "bill_user_sum2_gprs",
            "bill_user_sum2_gsm",
            "bill_user_sum2_sms",
            "td_a_fixtime_task",
            "td_a_job_source",
            "td_a_oper_cont",
            "td_a_oper_cont_var",
            "td_a_template_condition",
            "td_a_template_filter",
            "td_a_template_follow_oper",
            "td_a_template_oper",
            "td_a_template_rule_type",
            "td_a_template_trigger",
            "td_a_template_trigger_filter",
            "td_a_template_trigger_obj",
            "td_a_template_trigger_oper",
            "td_a_trigger_oper_cont",
            "td_b_apn",
            "td_b_comm_plan",
            "td_b_comm_profile",
            "td_b_cycle",
            "td_b_data_stream",
            "td_b_plan_resource",
            "td_b_profile_apn",
            "td_b_profile_element",
            "td_b_profile_template",
            "td_b_provider",
            "td_b_rate_plan",
            "td_b_rate_plan_data",
            "td_b_rate_plan_level",
            "td_b_rate_plan_sms",
            "td_b_rate_plan_version",
            "td_b_rate_plan_voice",
            "td_b_sequence",
            "td_b_service",
            "td_b_template_element",
            "td_b_third_party_level",
            "td_b_third_party_plan",
            "td_b_third_party_version",
            "td_b_third_party_zone",
            "td_b_zone",
            "td_b_zone_apn",
            "td_b_zone_data_stream_group",
            "td_b_zone_provider",
            "td_m_account_category",
            "td_m_address",
            "td_m_auto_default",
            "td_m_colpermission",
            "td_m_colunm_setting",
            "td_m_contact",
            "td_m_depart",
            "td_m_grant_auth",
            "td_m_ip_range",
            "td_m_net_cover",
            "td_m_notice",
            "td_m_password_log",
            "td_m_permission",
            "td_m_price",
            "td_m_province",
            "td_m_role",
            "td_m_rolepermission",
            "td_m_security_policy",
            "td_m_service_config",
            "td_m_service_overcharge",
            "td_m_service_provider",
            "td_m_user",
            "td_m_user_role",
            "td_m_user_token",
            "td_s_attr_table",
            "td_s_automatic_attr",
            "td_s_changelog_attr",
            "td_s_code_code",
            "td_s_code_item",
            "td_s_colpermission",
            "td_s_commpara",
            "td_s_country",
            "td_s_cparam",
            "td_s_currency",
            "td_s_custom_field",
            "td_s_custom_field_enum",
            "td_s_custom_label",
            "td_s_data_dict",
            "td_s_device_state",
            "td_s_dict_type",
            "td_s_field_realation",
            "td_s_hlr",
            "td_s_lang",
            "td_s_language",
            "td_s_m2m",
            "td_s_mail_template",
            "td_s_mode",
            "td_s_operate",
            "td_s_opn",
            "td_s_oprate_send",
            "td_s_order_state",
            "td_s_rate_plan",
            "td_s_res_state",
            "td_s_section",
            "td_s_ship_order_state",
            "td_s_sim_config",
            "td_s_sim_type",
            "td_s_sms_template",
            "td_s_smscenter",
            "td_s_systemconf",
            "td_s_systemmenu",
            "td_s_tickets",
            "td_s_timezone",
            "tf_a_acct",
            "tf_b_batch_update_detail",
            "tf_b_changelog",
            "tf_b_operate",
            "tf_b_operate_booking",
            "tf_b_operate_device",
            "tf_b_operate_device_comm",
            "tf_b_operate_device_comm_plan",
            "tf_b_operate_device_item",
            "tf_b_operate_device_rate",
            "tf_b_operate_device_rate_item",
            "tf_b_operate_device_rate_paln",
            "tf_b_operate_device_res",
            "tf_b_operate_device_state",
            "tf_b_operate_item",
            "tf_b_operate_renewal_rate_queue",
            "tf_b_operate_sharepool",
            "tf_b_operate_sim",
            "tf_b_operate_sms",
            "tf_b_operate_sub_item",
            "tf_b_operate_term_user",
            "tf_b_subscribe",
            "tf_b_subscribe_batdeal",
            "tf_b_trade",
            "tf_b_trade_product",
            "tf_b_trade_user",
            "tf_b_trade_user_item",
            "tf_bh_operate",
            "tf_bh_trade",
            "tf_f_acct",
            "tf_f_acct_adjust_before",
            "tf_f_acct_application",
            "tf_f_sharepool",
            "tf_f_sms",
            "tf_f_sp_group",
            "tf_f_sub_acct",
            "tf_f_sub_acct_addr",
            "tf_f_sub_acct_contact",
            "tf_f_term_user",
            "tf_f_transfer_record",
            "tf_f_user",
            "tf_f_user_item",
            "tf_f_user_product",
            "tf_fh_acct",
            "tf_l_subscribe_error_log",
            "tf_operate_b_sms",
            "tf_r_area",
            "tf_r_service_provider",
            "tf_r_sim_info",
            "ti_a_follow_oper_cont",
            "ti_a_oper_cont",
            "ti_o_mail",
            "ti_o_sms",
            "tl_diff_data",
            "ts_acct_bill_01",
            "ts_acct_bill_02",
            "ts_acct_bill_03",
            "ts_acct_bill_04",
            "ts_acct_bill_05",
            "ts_acct_bill_06",
            "ts_acct_bill_07",
            "ts_acct_bill_08",
            "ts_acct_bill_09",
            "ts_acct_bill_10",
            "ts_acct_bill_11",
            "ts_acct_bill_12",
            "ts_acct_bill_expect",
            "ts_acct_bill_track_01",
            "ts_acct_bill_track_02",
            "ts_acct_bill_track_03",
            "ts_acct_bill_track_04",
            "ts_acct_bill_track_05",
            "ts_acct_bill_track_06",
            "ts_acct_bill_track_07",
            "ts_acct_bill_track_08",
            "ts_acct_bill_track_09",
            "ts_acct_bill_track_10",
            "ts_acct_bill_track_11",
            "ts_acct_bill_track_12",
            "ts_acct_bill_track_expect",
            "ts_acct_order_01",
            "ts_acct_order_02",
            "ts_acct_order_03",
            "ts_acct_order_04",
            "ts_acct_order_05",
            "ts_acct_order_06",
            "ts_acct_order_07",
            "ts_acct_order_08",
            "ts_acct_order_09",
            "ts_acct_order_10",
            "ts_acct_order_11",
            "ts_acct_order_12",
            "ts_acct_order_expect",
            "ts_acct_usage_01",
            "ts_acct_usage_02",
            "ts_acct_usage_03",
            "ts_acct_usage_04",
            "ts_acct_usage_05",
            "ts_acct_usage_06",
            "ts_acct_usage_07",
            "ts_acct_usage_08",
            "ts_acct_usage_09",
            "ts_acct_usage_10",
            "ts_acct_usage_11",
            "ts_acct_usage_12",
            "ts_acct_usage_expect",
            "ts_add_device_01",
            "ts_add_device_02",
            "ts_add_device_03",
            "ts_add_device_04",
            "ts_add_device_05",
            "ts_add_device_06",
            "ts_add_device_07",
            "ts_add_device_08",
            "ts_add_device_09",
            "ts_add_device_10",
            "ts_add_device_11",
            "ts_add_device_12",
            "ts_add_device_expect",
            "ts_add_poolshare_01",
            "ts_add_poolshare_02",
            "ts_add_poolshare_03",
            "ts_add_poolshare_04",
            "ts_add_poolshare_05",
            "ts_add_poolshare_06",
            "ts_add_poolshare_07",
            "ts_add_poolshare_08",
            "ts_add_poolshare_09",
            "ts_add_poolshare_10",
            "ts_add_poolshare_11",
            "ts_add_poolshare_12",
            "ts_add_poolshare_expect",
            "ts_add_share_01",
            "ts_add_share_02",
            "ts_add_share_03",
            "ts_add_share_04",
            "ts_add_share_05",
            "ts_add_share_06",
            "ts_add_share_07",
            "ts_add_share_08",
            "ts_add_share_09",
            "ts_add_share_10",
            "ts_add_share_11",
            "ts_add_share_12",
            "ts_add_share_expect",
            "ts_add_sharedetail_01",
            "ts_add_sharedetail_02",
            "ts_add_sharedetail_03",
            "ts_add_sharedetail_04",
            "ts_add_sharedetail_05",
            "ts_add_sharedetail_06",
            "ts_add_sharedetail_07",
            "ts_add_sharedetail_08",
            "ts_add_sharedetail_09",
            "ts_add_sharedetail_10",
            "ts_add_sharedetail_11",
            "ts_add_sharedetail_12",
            "ts_add_sharedetail_expect",
            "ts_add_thirdsum_01",
            "ts_add_thirdsum_02",
            "ts_add_thirdsum_03",
            "ts_add_thirdsum_04",
            "ts_add_thirdsum_05",
            "ts_add_thirdsum_06",
            "ts_add_thirdsum_07",
            "ts_add_thirdsum_08",
            "ts_add_thirdsum_09",
            "ts_add_thirdsum_10",
            "ts_add_thirdsum_11",
            "ts_add_thirdsum_12",
            "ts_b_activebill_01",
            "ts_b_activebill_02",
            "ts_b_activebill_03",
            "ts_b_activebill_04",
            "ts_b_activebill_05",
            "ts_b_activebill_06",
            "ts_b_activebill_07",
            "ts_b_activebill_08",
            "ts_b_activebill_09",
            "ts_b_activebill_10",
            "ts_b_activebill_11",
            "ts_b_activebill_12",
            "ts_b_activebill_expect",
            "ts_b_addbill_01",
            "ts_b_addbill_02",
            "ts_b_addbill_03",
            "ts_b_addbill_04",
            "ts_b_addbill_05",
            "ts_b_addbill_06",
            "ts_b_addbill_07",
            "ts_b_addbill_08",
            "ts_b_addbill_09",
            "ts_b_addbill_10",
            "ts_b_addbill_11",
            "ts_b_addbill_12",
            "ts_b_addbill_expect",
            "ts_b_adjustbill_01",
            "ts_b_adjustbill_02",
            "ts_b_adjustbill_03",
            "ts_b_adjustbill_04",
            "ts_b_adjustbill_05",
            "ts_b_adjustbill_06",
            "ts_b_adjustbill_07",
            "ts_b_adjustbill_08",
            "ts_b_adjustbill_09",
            "ts_b_adjustbill_10",
            "ts_b_adjustbill_11",
            "ts_b_adjustbill_12",
            "ts_b_adjustbill_expect",
            "ts_b_device_activation",
            "ts_b_device_order",
            "ts_b_device_usage",
            "ts_b_device_usage_01",
            "ts_b_device_usage_02",
            "ts_b_device_usage_03",
            "ts_b_device_usage_04",
            "ts_b_device_usage_05",
            "ts_b_device_usage_06",
            "ts_b_device_usage_07",
            "ts_b_device_usage_08",
            "ts_b_device_usage_09",
            "ts_b_device_usage_10",
            "ts_b_device_usage_11",
            "ts_b_device_usage_12",
            "ts_b_device_usage_expect",
            "ts_b_devicebill_01",
            "ts_b_devicebill_02",
            "ts_b_devicebill_03",
            "ts_b_devicebill_04",
            "ts_b_devicebill_05",
            "ts_b_devicebill_06",
            "ts_b_devicebill_07",
            "ts_b_devicebill_08",
            "ts_b_devicebill_09",
            "ts_b_devicebill_10",
            "ts_b_devicebill_11",
            "ts_b_devicebill_12",
            "ts_b_devicebill_expect",
            "ts_b_discountbill_01",
            "ts_b_discountbill_02",
            "ts_b_discountbill_03",
            "ts_b_discountbill_04",
            "ts_b_discountbill_05",
            "ts_b_discountbill_06",
            "ts_b_discountbill_07",
            "ts_b_discountbill_08",
            "ts_b_discountbill_09",
            "ts_b_discountbill_10",
            "ts_b_discountbill_11",
            "ts_b_discountbill_12",
            "ts_b_discountbill_expect",
            "ts_b_gprsbill_01",
            "ts_b_gprsbill_02",
            "ts_b_gprsbill_03",
            "ts_b_gprsbill_04",
            "ts_b_gprsbill_05",
            "ts_b_gprsbill_06",
            "ts_b_gprsbill_07",
            "ts_b_gprsbill_08",
            "ts_b_gprsbill_09",
            "ts_b_gprsbill_10",
            "ts_b_gprsbill_11",
            "ts_b_gprsbill_12",
            "ts_b_gprsbill_expect",
            "ts_b_groupbill_01",
            "ts_b_groupbill_02",
            "ts_b_groupbill_03",
            "ts_b_groupbill_04",
            "ts_b_groupbill_05",
            "ts_b_groupbill_06",
            "ts_b_groupbill_07",
            "ts_b_groupbill_08",
            "ts_b_groupbill_09",
            "ts_b_groupbill_10",
            "ts_b_groupbill_11",
            "ts_b_groupbill_12",
            "ts_b_groupbill_expect",
            "ts_b_otherbill_01",
            "ts_b_otherbill_02",
            "ts_b_otherbill_03",
            "ts_b_otherbill_04",
            "ts_b_otherbill_05",
            "ts_b_otherbill_06",
            "ts_b_otherbill_07",
            "ts_b_otherbill_08",
            "ts_b_otherbill_09",
            "ts_b_otherbill_10",
            "ts_b_otherbill_11",
            "ts_b_otherbill_12",
            "ts_b_otherbill_expect",
            "ts_b_planbill_01",
            "ts_b_planbill_02",
            "ts_b_planbill_03",
            "ts_b_planbill_04",
            "ts_b_planbill_05",
            "ts_b_planbill_06",
            "ts_b_planbill_07",
            "ts_b_planbill_08",
            "ts_b_planbill_09",
            "ts_b_planbill_10",
            "ts_b_planbill_11",
            "ts_b_planbill_12",
            "ts_b_planbill_expect",
            "ts_b_planzonebill_01",
            "ts_b_planzonebill_02",
            "ts_b_planzonebill_03",
            "ts_b_planzonebill_04",
            "ts_b_planzonebill_05",
            "ts_b_planzonebill_06",
            "ts_b_planzonebill_07",
            "ts_b_planzonebill_08",
            "ts_b_planzonebill_09",
            "ts_b_planzonebill_10",
            "ts_b_planzonebill_11",
            "ts_b_planzonebill_12",
            "ts_b_planzonebill_expect",
            "ts_b_prepaybill_01",
            "ts_b_prepaybill_02",
            "ts_b_prepaybill_03",
            "ts_b_prepaybill_04",
            "ts_b_prepaybill_05",
            "ts_b_prepaybill_06",
            "ts_b_prepaybill_07",
            "ts_b_prepaybill_08",
            "ts_b_prepaybill_09",
            "ts_b_prepaybill_10",
            "ts_b_prepaybill_11",
            "ts_b_prepaybill_12",
            "ts_b_prepaybill_expect",
            "ts_b_rategroup_discountbill_01",
            "ts_b_rategroup_discountbill_02",
            "ts_b_rategroup_discountbill_03",
            "ts_b_rategroup_discountbill_04",
            "ts_b_rategroup_discountbill_05",
            "ts_b_rategroup_discountbill_06",
            "ts_b_rategroup_discountbill_07",
            "ts_b_rategroup_discountbill_08",
            "ts_b_rategroup_discountbill_09",
            "ts_b_rategroup_discountbill_10",
            "ts_b_rategroup_discountbill_11",
            "ts_b_rategroup_discountbill_12",
            "ts_b_rategroup_discountbill_expect",
            "ts_b_smbill_01",
            "ts_b_smbill_02",
            "ts_b_smbill_03",
            "ts_b_smbill_04",
            "ts_b_smbill_05",
            "ts_b_smbill_06",
            "ts_b_smbill_07",
            "ts_b_smbill_08",
            "ts_b_smbill_09",
            "ts_b_smbill_10",
            "ts_b_smbill_11",
            "ts_b_smbill_12",
            "ts_b_smbill_expect",
            "ts_b_sumbill_01",
            "ts_b_sumbill_02",
            "ts_b_sumbill_03",
            "ts_b_sumbill_04",
            "ts_b_sumbill_05",
            "ts_b_sumbill_06",
            "ts_b_sumbill_07",
            "ts_b_sumbill_08",
            "ts_b_sumbill_09",
            "ts_b_sumbill_10",
            "ts_b_sumbill_11",
            "ts_b_sumbill_12",
            "ts_b_sumbill_expect",
            "ts_b_voicebill_01",
            "ts_b_voicebill_02",
            "ts_b_voicebill_03",
            "ts_b_voicebill_04",
            "ts_b_voicebill_05",
            "ts_b_voicebill_06",
            "ts_b_voicebill_07",
            "ts_b_voicebill_08",
            "ts_b_voicebill_09",
            "ts_b_voicebill_10",
            "ts_b_voicebill_11",
            "ts_b_voicebill_12",
            "ts_b_voicebill_expect",
            "ts_bss_bill_01",
            "ts_bss_bill_02",
            "ts_bss_bill_03",
            "ts_bss_bill_04",
            "ts_bss_bill_05",
            "ts_bss_bill_06",
            "ts_bss_bill_07",
            "ts_bss_bill_08",
            "ts_bss_bill_09",
            "ts_bss_bill_10",
            "ts_bss_bill_11",
            "ts_bss_bill_12",
            "ts_bss_bill_expect",
            "ts_device_activation_01",
            "ts_device_activation_02",
            "ts_device_activation_03",
            "ts_device_activation_04",
            "ts_device_activation_05",
            "ts_device_activation_06",
            "ts_device_activation_07",
            "ts_device_activation_08",
            "ts_device_activation_09",
            "ts_device_activation_10",
            "ts_device_activation_11",
            "ts_device_activation_12",
            "ts_device_activation_expect",
            "ts_device_order_01",
            "ts_device_order_02",
            "ts_device_order_03",
            "ts_device_order_04",
            "ts_device_order_05",
            "ts_device_order_06",
            "ts_device_order_07",
            "ts_device_order_08",
            "ts_device_order_09",
            "ts_device_order_10",
            "ts_device_order_11",
            "ts_device_order_12",
            "ts_device_order_expect",
            "ts_device_usage_01",
            "ts_device_usage_02",
            "ts_device_usage_03",
            "ts_device_usage_04",
            "ts_device_usage_05",
            "ts_device_usage_06",
            "ts_device_usage_07",
            "ts_device_usage_08",
            "ts_device_usage_09",
            "ts_device_usage_10",
            "ts_device_usage_11",
            "ts_device_usage_12",
            "ts_device_usage_expect",
            "ts_r_agile_share_turn",
            "ts_r_fix_share_turn",
            "ts_res_agileshare_turn_01",
            "ts_res_agileshare_turn_02",
            "ts_res_agileshare_turn_03",
            "ts_res_agileshare_turn_04",
            "ts_res_agileshare_turn_05",
            "ts_res_agileshare_turn_06",
            "ts_res_agileshare_turn_07",
            "ts_res_agileshare_turn_08",
            "ts_res_agileshare_turn_09",
            "ts_res_agileshare_turn_10",
            "ts_res_agileshare_turn_11",
            "ts_res_agileshare_turn_12",
            "ts_res_agileshare_turn_expect",
            "ts_res_device_01",
            "ts_res_device_02",
            "ts_res_device_03",
            "ts_res_device_04",
            "ts_res_device_05",
            "ts_res_device_06",
            "ts_res_device_07",
            "ts_res_device_08",
            "ts_res_device_09",
            "ts_res_device_10",
            "ts_res_device_11",
            "ts_res_device_12",
            "ts_res_device_expect",
            "ts_res_fixshare_turn_01",
            "ts_res_fixshare_turn_02",
            "ts_res_fixshare_turn_03",
            "ts_res_fixshare_turn_04",
            "ts_res_fixshare_turn_05",
            "ts_res_fixshare_turn_06",
            "ts_res_fixshare_turn_07",
            "ts_res_fixshare_turn_08",
            "ts_res_fixshare_turn_09",
            "ts_res_fixshare_turn_10",
            "ts_res_fixshare_turn_11",
            "ts_res_fixshare_turn_12",
            "ts_res_fixshare_turn_expect",
            "ts_res_pile_01",
            "ts_res_pile_02",
            "ts_res_pile_03",
            "ts_res_pile_04",
            "ts_res_pile_05",
            "ts_res_pile_06",
            "ts_res_pile_07",
            "ts_res_pile_08",
            "ts_res_pile_09",
            "ts_res_pile_10",
            "ts_res_pile_11",
            "ts_res_pile_12",
            "ts_res_pile_expect",
            "ts_res_pool_01",
            "ts_res_pool_02",
            "ts_res_pool_03",
            "ts_res_pool_04",
            "ts_res_pool_05",
            "ts_res_pool_06",
            "ts_res_pool_07",
            "ts_res_pool_08",
            "ts_res_pool_09",
            "ts_res_pool_10",
            "ts_res_pool_11",
            "ts_res_pool_12",
            "ts_res_pool_expect",
            "ts_res_share_01",
            "ts_res_share_02",
            "ts_res_share_03",
            "ts_res_share_04",
            "ts_res_share_05",
            "ts_res_share_06",
            "ts_res_share_07",
            "ts_res_share_08",
            "ts_res_share_09",
            "ts_res_share_10",
            "ts_res_share_11",
            "ts_res_share_12",
            "ts_res_share_expect",
            "ts_usedres_device_01",
            "ts_usedres_device_02",
            "ts_usedres_device_03",
            "ts_usedres_device_04",
            "ts_usedres_device_05",
            "ts_usedres_device_06",
            "ts_usedres_device_07",
            "ts_usedres_device_08",
            "ts_usedres_device_09",
            "ts_usedres_device_10",
            "ts_usedres_device_11",
            "ts_usedres_device_12",
            "ts_usedres_device_expect",
            "ts_usedres_pool_01",
            "ts_usedres_pool_02",
            "ts_usedres_pool_03",
            "ts_usedres_pool_04",
            "ts_usedres_pool_05",
            "ts_usedres_pool_06",
            "ts_usedres_pool_07",
            "ts_usedres_pool_08",
            "ts_usedres_pool_09",
            "ts_usedres_pool_10",
            "ts_usedres_pool_11",
            "ts_usedres_pool_12",
            "ts_usedres_pool_expect",
            "ts_usedres_share_01",
            "ts_usedres_share_02",
            "ts_usedres_share_03",
            "ts_usedres_share_04",
            "ts_usedres_share_05",
            "ts_usedres_share_06",
            "ts_usedres_share_07",
            "ts_usedres_share_08",
            "ts_usedres_share_09",
            "ts_usedres_share_10",
            "ts_usedres_share_11",
            "ts_usedres_share_12",
            "ts_usedres_share_expect"
    };
		/*for(int i=0; i<tables.length; i++){
			for(int j=1; j<=12;j++){
				String table = String.format(tables[i], j);
				String s = "<table name=\""+table+"\" dataNode=\"info_dn3,info_dn5,info_dn7,info_dn9\"/>";
				System.out.println(s);
			}
			System.out.println("");
		}*/
    for(int i=0; i<tables.length; i++){
      String s = "<table name=\""+tables[i]+"\" dataNode=\"info_dn3,info_dn5,info_dn7,info_dn9\"/>";
      System.out.println(s);
    }
  }

  @Test
  public void test10() {
    List<String> list = null;
    Map<String, String> props = new HashMap<>();
    props.put("group.id", "iot");
    props.put("max.poll.records", "1");
//		props.put("auto.offset.reset", "earliest");
    KafkaMq kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_AUTORULE, Const.READ_ONLY, props);

    while (true) {
      kafkaMq.setTopic("AUTORULE_TEST_IN");
      list = kafkaMq.recvMsgs(2000);
      System.out.println(list.isEmpty());
      if (ListUtil.isNotEmpty(list)) {
        System.out.println("================AUTORULE_TEST_IN==============" + list.size());
        for (String s : list) {
          System.out.println(s);
        }
      }
      kafkaMq.commit();
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
