package com.common.frame;

public class ResInfoZkResourceManagerFactory {
	public static ResInfoZkResourceManager newResourceManagerInstance(String zkConnectList, String zkRoot) {
		return new ResInfoZkResourceManagerImp(zkConnectList,zkRoot);
	}
}
