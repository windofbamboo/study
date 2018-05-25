package com.common.frame;

import com.common.util.Const;

public class ResInfoZkClientFactory {
	public static ResInfoZkClient newClientInstance(String zkConnectList, String zkRoot,
			Const.ModuleNameEnum moduleName, String channelId) {
		return new ResInfoZkClientImp(zkConnectList, zkRoot, moduleName, channelId);
	}

}
