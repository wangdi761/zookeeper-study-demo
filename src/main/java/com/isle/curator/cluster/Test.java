package com.isle.curator.cluster;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Test {

	/** zookeeper地址 */
	static final String CONNECT_ADDR = "192.168.93.134:2181";
	/** session超时时间 */
	static final int SESSION_OUTTIME = 5000;//ms 
	
	public static void main(String[] args) throws Exception {
		
		//1 重试策略：初试时间为1s 重试10次
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
		//2 通过工厂创建连接
		CuratorFramework cf = CuratorFrameworkFactory.builder()
					.connectString(CONNECT_ADDR)
					.sessionTimeoutMs(SESSION_OUTTIME)
					.retryPolicy(retryPolicy)
					.build();
		//3 开启连接
		cf.start();
		
		//4 创建节点
		cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/super/c1","c1内容".getBytes());
		Thread.sleep(1000);
		cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/super/c2","c2内容".getBytes());
		Thread.sleep(1000);
		//6 修改节点
		Thread.sleep(1000);
		cf.setData().forPath("/super/c2", "修改的新c2内容".getBytes());
		String ret2 = new String(cf.getData().forPath("/super/c2"));
		System.out.println(ret2);
		
	}
}
