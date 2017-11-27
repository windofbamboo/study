package com.ai.iot.mdb.common.mdb.mog;

import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.mdb.common.autorule.mos.MoAutoRuleWrapper;
import com.ai.iot.mdb.common.autorule.mos.MoAutorule;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleOperCont;
import com.ai.iot.mdb.common.autorule.mos.MoSubAcct;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import com.ai.iot.mdb.common.mdb.MoGroup;

/**
 * @author qianwx
 */
public class AutoRuleMoGroup extends MoGroup {

  public AutoRuleMoGroup() {
    super.name = MoBaseRegisterFactory.MoGroupEnum.MOG_AUTORULE.toString();

    MoBase moBase = new MoAutoRuleWrapper();
    moBases.put(moBase.getName(), moBase);
    moBase = new MoAutorule();
    moBases.put(moBase.getName(), moBase);

    moBase = new MoAutoruleOperCont();
    moBases.put(moBase.getName(), moBase);
    
    moBase = new MoSubAcct();
    moBases.put(moBase.getName(), moBase);

  }
}
