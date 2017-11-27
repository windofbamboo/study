package com.ai.iot.bill.define;

/**错误码定义
 * Created by geyunfeng on 2017/8/14.
 */
public final class ErrEnum {

  public final class DealTag {
    public static final String INTI  = "0"; //初始化
    public static final String START = "1"; //开始
    public static final String ERR   = "2"; //失败
    public static final String SUCCESS = "3"; //成功
  }

  public final class AcctDealResult{
    public static final int GET    = 1; // 获取到资料
    public static final int IGNORE = 2; // 不处理
    public static final int FAIL   = 3; // 处理失败
    public static final int SUCESS = 4; // 处理成功
  }

  public static final class DealResult{
    private int resultNo;
    private String resultInfo;

    DealResult(int resultNo, String resultInfo) {
      this.resultNo = resultNo;
      this.resultInfo = resultInfo;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DealResult that = (DealResult) o;

      return resultNo == that.resultNo;
    }

    @Override
    public int hashCode() {
      return resultNo;
    }

    public int getResultNo() {
      return resultNo;
    }

    public String getResultInfo() {
      return resultInfo;
    }
  }


  /**
   * 处理情况
   */
  public static final DealResult DEAL_START  = new DealResult(1,"START");  // 单步处理开始
  public static final DealResult DEAL_SUCESS = new DealResult(3,"SUCESS"); // 单步处理成功

  /**
   * 数据库操作相关错误
   */
  public static final class dbErr {
    public static final DealResult GET_DATASOURCE_ERR = new DealResult(1001,"GET_DATASOURCE_ERR"); // 不能建立dataSource
    public static final DealResult GET_CONNECT_ERR    = new DealResult(1002,"GET_CONNECT_ERR"); // 不能获取Connection
    public static final DealResult SELECT_ERR         = new DealResult(1003,"SELECT_ERR"); // 获取数据错误
    public static final DealResult UPDATE_ERR         = new DealResult(1004,"UPDATE_ERR"); // 更新数据错误
    public static final DealResult INSERT_ERR         = new DealResult(1005,"INSERT_ERR"); // 插入数据错误
    public static final DealResult DELETE_ERR         = new DealResult(1006,"DELETE_ERR"); // 删除数据错误
  }

  public static final class ParamErr{
    public static final DealResult PARAM_INIT_ERR = new DealResult(1101,"PARAM_INIT_ERR");
  }

  /**
   * 资料相关错误
   */
  public static final class InfoErr {
    public static final DealResult GET_ACCT_INFO_ERR       = new DealResult(1201,"GET_ACCT_INFO_ERR");
    public static final DealResult ACCT_INFO_INTACT        = new DealResult(1202,"ACCT_INFO_INTACT");
    public static final DealResult NOT_EXIST_ACTICE_DEVICE = new DealResult(1203,"NOT_EXIST_ACTICE_DEVICE");
    public static final DealResult GET_DEVICE_INFO_ERR     = new DealResult(1204,"GET_DEVICE_INFO_ERR");
    public static final DealResult DEVICE_INFO_INTACT      = new DealResult(1205,"DEVICE_INFO_INTACT");
    public static final DealResult DEVICE_STATE_INACTIVE   = new DealResult(1206,"DEVICE_STATE_INACTIVE");
  }

  public static final class DealErr{
    public static final DealResult DEVICE_GET_ADD     = new DealResult(1301,"get add err");
    public static final DealResult DEVICE_COMPUTE_FEE = new DealResult(1302,"device compute fee err");

    public static final DealResult ACCT_GET_ADD     = new DealResult(1304,"ACCT_GET_ADD") ;
    public static final DealResult ACCT_COMPUTE_FEE = new DealResult(1305,"ACCT_COMPUTE_FEE") ;
    public static final DealResult ACCT_MULTI_BILL  = new DealResult(1306,"ACCT_MULTI_BILL") ;
    public static final DealResult UPDATE_ACCT_BILL = new DealResult(1307,"UPDATE_ACCT_BILL") ;
  }


}
