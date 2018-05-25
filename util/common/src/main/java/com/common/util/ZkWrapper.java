package com.common.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
/**
 * 普通节点的insert操作=插入+更新
 * 临时节点的insert操作=插入，如果已经存在会抛异常
 * */
public class ZkWrapper {
	public static final String ZK_PATH_SEP = "/";
	private Logger logger = LoggerFactory.getLogger(ZkWrapper.class);
	public static String ROOT_DEFAULT_NAME="/roam";
	private String root = ROOT_DEFAULT_NAME; // zk root path
	private static final int connectionTimeoutMs = 5000;// 默认 zk 超时时长

	private static final String shareLock = "shareLock";// 分布式锁路径

	private static final long defaultTimeToWait = 1000L;
	private static final TimeUnit defaultTimeUnit = TimeUnit.MILLISECONDS;

	/**
	 * scheme: scheme对应于采用哪种方案来进行权限管理
	 * <p>
	 * world: 它下面只有一个id, 叫anyone,
	 * world:anyone代表任何人，zookeeper中对所有人有权限的结点就是属于world:anyone的 auth: 它不需要id,
	 * 只要是通过authentication的user都有权限（zookeeper支持通过kerberos来进行authencation,
	 * 也支持username/password形式的authentication) digest:
	 * 它对应的id为username:BASE64(SHA1(password))，它需要先通过username:password形式的authentication
	 * ip: 它对应的id为客户机的IP地址，设置的时候可以设置一个ip段，比如ip:192.168.1.0/16, 表示匹配前16个bit的IP段
	 * super: 在这种scheme情况下，对应的id拥有超级权限，可以做任何事情(cdrwa)
	 */
	private static final String scheme = "digest";

	private String connStr;// zk 连接

	private String auth;// zk 验证

	private CuratorFramework client;// curator

	private RetryPolicy retryPolicy;// zk 连接重试策略
	private String defaultData = null;

	public ZkWrapper(String connStr, String auth, String root) {
		this.connStr = connStr;
		this.auth = auth;
		this.root = root;
	}

	public ZkWrapper(String connStr, String root) {
		retryPolicy = new RetryNTimes(Integer.MAX_VALUE, 3000);
		this.connStr = connStr;
		this.root = root;
	}

	/**
	 * 获取curator 客户端
	 */
	private void getClient() throws Exception {
		if (null == client) {
			CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
			if (StringUtils.isNotBlank(auth)) {
				ACLProvider aclProvider = new ACLProvider() {
					private List<ACL> acl;

					@Override
					public List<ACL> getDefaultAcl() {
						if (acl == null) {
							ArrayList<ACL> acl = ZooDefs.Ids.CREATOR_ALL_ACL;
							acl.clear();
							acl.add(new ACL(ZooDefs.Perms.ALL, new Id("auth", "admin:admin")));
							this.acl = acl;
						}
						return acl;
					}

					@Override
					public List<ACL> getAclForPath(String path) {
						return acl;
					}
				};
				byte[] authBytes = auth.getBytes(Const.UTF8);
				builder = builder.aclProvider(aclProvider).authorization(scheme, authBytes);
			}
			builder = builder.connectionTimeoutMs(connectionTimeoutMs).connectString(connStr).retryPolicy(retryPolicy);
			defaultData = new String(builder.getDefaultData());
			client = builder.build();
			client.start();
			if (client.checkExists().forPath(root) == null) {
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(root);
			}
		}
	}

	/**
	 * 关闭curator连接
	 */
	public void closeConn() {
		if (client != null) {
			client.close();
			client = null;
		}
	}

	/**
	 * 判断节点是否存在
	 */
	public boolean exists(String node) throws Exception {
		getClient();
		return client.checkExists().forPath(fullPath(node)) != null;
	}

	/**
	 * 判断临时节点是否存在
	 */
	public boolean isTempNodeExist(String node) throws Exception {
		getClient();
		return client.checkExists().forPath(fullPath(node)) != null;
	}

	/**
	 * 获取子目录
	 */
	public List<String> getChildNodeList(String node) throws Exception {
		getClient();
		if(client.checkExists().forPath(fullPath(node)) == null) {
			return Collections.emptyList();
		}
		return client.getChildren().forPath(fullPath(node));
	}

	/**
	 * 获取节点内容
	 */
	public String getNodeValue(String node) throws Exception {
		getClient();
		if(client.checkExists().forPath(fullPath(node)) == null) {
			return "";
		}
		return new String(client.getData().forPath(fullPath(node)));
	}

	/**
	 * 创建永久节点
	 */
	public void insertNode(String node) throws Exception {
		insertNode(node, CreateMode.PERSISTENT);
	}

	/**
	 * 创建永久节点,并设置消息
	 */
	public void insertNode(String node, String message) throws Exception {
		createAndUpdate(node, message, CreateMode.PERSISTENT);
	}

	/**
	 * 设置节点内容
	 */
	public void updateNodeValue(String node, String msg) throws Exception {
		getClient();
		if (StringUtils.isNotBlank(msg)) {
			client.setData().forPath(fullPath(node), msg.getBytes(Const.UTF8));
		}
	}

	/**
	 * 创建临时节点,并设置消息，临时节点和节点内容只允许设置一次。
	 * 临时节点的创建是排他性
	 */
	public void insertTempNode(String node, String message) throws Exception {
		getClient();
//		// 如果临时节点，原本就存在，就等待释放，放到业务层处理
//		for (;;) {
//			if (client.checkExists().forPath(fullPath(node)) != null) {
//				String data = new String(client.getData().forPath(fullPath(node)));
//				if (data.equals(message)) {
//					return;
//				} else {
//					Thread.sleep(1000);
//				}
//			} else {
//				break;
//			}
//		}
		//因为临时节点的创建是排他性的，所以不需要判断是否存在
		//if(!(client.checkExists().forPath(fullPath(node)) != null)) {
			create(node, message, CreateMode.EPHEMERAL);
		//}
		//为避免其他进程重复覆盖值，所以有必要使用分布式锁来避免并发修改
//		if(!(client.checkExists().forPath(fullPath(node)) != null)) {
//			distributedWork(-1L, null, () -> {
//				if(!(client.checkExists().forPath(fullPath(node)) != null)) {
//					create(node, message, CreateMode.EPHEMERAL);
//				}
//			});
//		}		
	}

	/**
	 * 删除临时节点
	 */
	public void deleteTempNode(String node) throws Exception {
		getClient();
		if(client.checkExists().forPath(fullPath(node)) != null) {
			List<String> children = client.getChildren().forPath(fullPath(node));
			if (children.isEmpty()) {
				client.delete().quietly().guaranteed().forPath(fullPath(node));
			}
		}
//		if(client.checkExists().forPath(fullPath(node)) != null) {
//			distributedWork(-1L, null, () -> {
//				if(client.checkExists().forPath(fullPath(node)) != null) {
//					List<String> children = client.getChildren().forPath(fullPath(node));
//					if (children.isEmpty()) {
//						client.delete().quietly().guaranteed().forPath(fullPath(node));
//					}
//				}
//			});
//		}		
	}

	/**
	 * 删除节点及子目录
	 */
	public void deleteNode(String node) throws Exception {
		getClient();
		if (client.checkExists().forPath(fullPath(node)) != null) {
			client.delete().deletingChildrenIfNeeded().forPath(fullPath(node));
		}
	}

	/**
	 * 创建mode类型节点
	 *
	 * @param node
	 * @param mode
	 * @throws Exception
	 */
	private void insertNode(String node, CreateMode mode) throws Exception {
		createAndUpdate(node, null, mode);
	}

	/**
	 * 创建mode 类型节点，并设置消息
	 *
	 * @param node
	 * @param message
	 * @param mode
	 * @throws Exception
	 */
	private void create(String node, String message, CreateMode mode) throws Exception {
		getClient();
		String path = fullPath(node);
		//如果仅仅是创建，不能判断其存在性
		//if (client.checkExists().forPath(path) == null) {
			if (StringUtils.isBlank(message)) {
				client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
			} else {
				client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, message.getBytes(Const.UTF8));
			}
		//} else {
		//	updateNodeValue(path, message);
		//}
	}

	private void createAndUpdate(String node, String message, CreateMode mode) throws Exception {
		getClient();
		String path = fullPath(node);
		if (client.checkExists().forPath(path) == null) {
			try {
				if (StringUtils.isBlank(message)) {
					client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);

				} else {
					client.create().creatingParentsIfNeeded().withMode(mode).forPath(path,
							message.getBytes(Const.UTF8));
				}
			} catch (NodeExistsException e) {
				updateNodeValue(path, message);
			}
		} else {
			updateNodeValue(path, message);
		}
	}

	/**
	 * 返回节点全路径
	 */
	private String fullPath(String node) {
		// 若路径是全路径，则返回
		if (node.startsWith(root + ZK_PATH_SEP) || node.equals(root)) {
			return node;
		}
		// 否则路径加上root
		return PathUtil.full_path(root, node);
	}

	public boolean lock(String lockPath) throws Exception {
		getClient();
		InterProcessMutex interProcessMutex = new InterProcessMutex(client, fullPath(lockPath));
		return interProcessMutex.acquire(3, TimeUnit.SECONDS);
	}

	public void unlock() throws Exception {
		closeConn();
	}

	public InterProcessMutex getDistributedLock() throws Exception {
		getClient();
		return new InterProcessMutex(client, fullPath(shareLock));
	}

	public InterProcessMutex getDistributedLock(String lockPath) throws Exception {
		getClient();
		return new InterProcessMutex(client, fullPath(lockPath));
	}

	/**
	 * 实现分布式线程锁任务 example : { ZkMgr zkMgr = new ZkMgr("127.0.0.1:2181");
	 * zkMgr.distributedWork(() -> { //do distributed work }); }
	 *
	 * @param worker
	 */
	public void distributedWork(ZkWrapper.Worker worker) throws Exception {
		distributedWork(defaultTimeToWait, defaultTimeUnit, worker);
	}

	/**
	 * 实现分布式线程锁任务
	 *
	 * @param time
	 *            time to wait to acquire mutex lock
	 * @param unit
	 *            time unit
	 * @param worker
	 *            Worker impl
	 * @throws Exception
	 */
	public void distributedWork(long time, TimeUnit unit, ZkWrapper.Worker worker) throws Exception {
		InterProcessMutex lock = getDistributedLock();
		if (!lock.acquire(time, unit)) {
			throw new IllegalStateException(Thread.currentThread().getName() + " could not acquire the lock");
		}
		try {
			worker.doWork();
		} finally {
			lock.release();
		}
	}

	/**
	 * 实现具体分布式任务
	 */
	public interface Worker {
		void doWork() throws Exception;
	}

	public interface ZkWatcherCallBack {
		void updateZkInfo(String node, String data);
	}
	public interface ZkWatcherTypeCallBack {
		void updateZkInfo(Type type, String node, String data);
	}

	public enum Type {
		ADDED, UPDATED, REMOVED
	}

	/**
	 * zk的均衡注册实现
	 */
	public List<Integer> setPartitionsForSpout(String rootPath, int taskId, int partitionNums, int taskNum)
			throws Exception {
		String path = rootPath + ZK_PATH_SEP + taskId;
		long now = System.currentTimeMillis();
		while (exists(path)) {// 防止快速重启，临时节点未及时删除的场景
			if (System.currentTimeMillis() - now > connectionTimeoutMs + 5000)
				break;
			Thread.sleep(1000);
		}
		if (exists(path)) {
			throw new BillException(BillException.BillExceptionENUM.ZK_PATH_ALREADY_EXISTS,
					"taskId已经存在,初始化时不应该存在!" + path);
		} else {
			// 判断是否存在 rootPath 节点，若不存在，则新建
			if (!exists(rootPath)) {
				insertNode(rootPath);
			}
			final Set<Integer> alreadyConsumedPartitions = new HashSet<>();
			final List<Integer> partitionNumList = new ArrayList<>();
			// 分布式线程锁执行任务
			distributedWork(10L, TimeUnit.SECONDS, () -> {
				// 获取所有已经注册的taskid
				List<String> tasks = getChildNodeList(rootPath);
				if (!CollectionUtils.isEmpty(tasks)) {
					// 添加已经绑定的分区
					tasks.forEach(s -> {
						try {
							String dataString = getNodeValue(rootPath + ZK_PATH_SEP + s);
							logger.error("taskidpath[{}]=[{}]", rootPath + ZK_PATH_SEP + s, dataString);
							if (dataString.equals(defaultData)) {
								return;
							}
							alreadyConsumedPartitions.addAll(Arrays.stream(StringUtils.split(dataString, ","))
									.map(i -> Integer.valueOf(i)).collect(Collectors.toList()));
						} catch (Exception e) {
							logger.error("ERROR:", e);
						}
					});
				}
				// 计算出分配给当前task的分区列表
				List<Integer> numList = SysUtil.getBalanceNumList(0, partitionNums - 1, taskNum,
						alreadyConsumedPartitions);

				for (Integer n : numList) {
					partitionNumList.add(n);
				}
				logger.error("total partitionnum={},current taskidpath[{}]=[{}]", partitionNums, path,
						StringUtils.join(partitionNumList, ","));
				if (org.apache.commons.collections.CollectionUtils.isEmpty(partitionNumList)) {
					throw new BillException(BillException.BillExceptionENUM.ZK_WRAPPER_IS_NULL,
							"partition List  is null!" + getClass().getName());
				}
				// 创建zk里边的path
				insertTempNode(path, StringUtils.join(partitionNumList, ","));
			});
			return partitionNumList;
		}
	}
	public void setListenterConnect(ConnectionStateListener connectionStateListener) throws Exception {
		// 注册连接检测的回调函数
		getClient();
		client.getConnectionStateListenable().addListener(connectionStateListener);
	}
	// 1.path Cache 连接 路径 是否获取数据
	// 能监听所有的字节点 且是无限监听的模式 但是 指定目录下节点的子节点不再监听
	public void setListenterChildrenNode(ZkWatcherTypeCallBack callBack, String node) throws Exception {
		getClient();
		String path = fullPath(node);

		@SuppressWarnings("resource")
		PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
		PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				ChildData data = event.getData();
				if (data != null) {
					switch (event.getType()) {
					case CHILD_ADDED:
						callBack.updateZkInfo(Type.ADDED, data.getPath(), new String(data.getData()));
						break;
					case CHILD_REMOVED:
						callBack.updateZkInfo(Type.REMOVED, data.getPath(), new String(data.getData()));
						break;
					case CHILD_UPDATED:
						callBack.updateZkInfo(Type.UPDATED, data.getPath(), new String(data.getData()));
						break;
					default:
						break;
					}
				}
			}
		};
		childrenCache.getListenable().addListener(childrenCacheListener);
		System.out.println("Register zk watcher successfully!");
		childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
		
	}
	
	public void deleteListenterChildrenNode(String node)  throws Exception {
		//暂不实现
	}

	// 2.Node Cache 监控本节点的变化情况 连接 目录 是否压缩
	// 监听本节点的变化 节点可以进行修改操作 删除节点后会再次创建(空节点)
	public void setListenterNode(ZkWatcherCallBack callBack, String node) throws Exception {
		getClient();
		String path = fullPath(node);
		// 设置节点的cache
		@SuppressWarnings("resource")
		final NodeCache nodeCache = new NodeCache(client, path, false);

		nodeCache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				callBack.updateZkInfo(nodeCache.getCurrentData().getPath(),
						new String(nodeCache.getCurrentData().getData()));
			}
		});

		nodeCache.start();
	}
	public void deleteListenterNode(String node)  throws Exception {
		//暂不实现
	}
	// 3.Tree Cache
	// 监控 指定节点和节点下的所有的节点的变化--无限监听 可以进行本节点的删除(不在创建)
	public void setListenterTree(ZkWatcherTypeCallBack callBack, String node) throws Exception {
		getClient();
		String path = fullPath(node);
		// 设置节点的cache
		@SuppressWarnings("resource")
		TreeCache treeCache = new TreeCache(client, path);
		TreeCacheListener treeCacheListener = new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				ChildData data = event.getData();
				if (data != null) {
					switch (event.getType()) {
						case NODE_ADDED:
							callBack.updateZkInfo(Type.ADDED, data.getPath(), new String(data.getData()));
							break;
						case NODE_REMOVED:
							callBack.updateZkInfo(Type.REMOVED, data.getPath(), new String(data.getData()));
							break;
						case NODE_UPDATED:
							callBack.updateZkInfo(Type.UPDATED, data.getPath(), new String(data.getData()));
							break;
						default:
							break;
					}
				}
			}
		};
		// 设置监听器和处理过程
		treeCache.getListenable().addListener(treeCacheListener);
		// 开始监听
		treeCache.start();
	}
	public void deleteListenterTree(String node)  throws Exception {
		//暂不实现
	}
}
