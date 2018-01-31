package com.isle.curator.base;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 61066 on 2018/1/30.
 */
public class CuratorBase {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;//ms

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        CuratorFramework cf = CuratorFrameworkFactory.builder()
                            .connectString(CONNECT_ADDR)
                            .sessionTimeoutMs(SESSION_OUTTIME)
                            .retryPolicy(retryPolicy)
                            .build();

        //开启连接
        cf.start();

        /*System.out.println(ZooKeeper.States.CONNECTED);
		System.out.println(cf.getState());*/

		/*cf.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/super/c1", "c1的内容".getBytes());*/

        /*cf.delete().guaranteed()
                .deletingChildrenIfNeeded()
                .forPath("/super");*/

        /*cf.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/super/c1", "c1".getBytes());

        cf.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/super/c2", "c2".getBytes());

        String ret1 = new String(cf.getData().forPath("/super/c1"));

        System.out.println(ret1);*/

        /*cf.setData().forPath("/super/c1", "新内容".getBytes());

        String ret1 = new String(cf.getData().forPath("/super/c1"));

        System.out.println(ret1);*/

        /*ExecutorService pool = Executors.newCachedThreadPool();

        cf.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println("code:" + curatorEvent.getResultCode());
                    System.out.println("type:" + curatorEvent.getType());
                    System.out.println("线程为:" + Thread.currentThread().getName());
                }, pool)
                .forPath("/super/c3", "c3".getBytes());

        Thread.sleep(Integer.MAX_VALUE);*/

        List<String> list = cf.getChildren().forPath("/super");
        for (String s : list) {
            System.out.println(s);
        }

        Stat stat = cf.checkExists().forPath("/super/c3");
        System.out.println(stat);
        Thread.sleep(2000);
        cf.delete().guaranteed().deletingChildrenIfNeeded().forPath("/super");
		cf.close();



    }

}
