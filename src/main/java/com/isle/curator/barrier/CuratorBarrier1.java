package com.isle.curator.barrier;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Random;

/**
 * Created by 61066 on 2018/1/31.
 */
public class CuratorBarrier1 {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;//ms

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
                CuratorFramework cf = CuratorFrameworkFactory.builder()
                        .connectString(CONNECT_ADDR)
                        .sessionTimeoutMs(SESSION_OUTTIME)
                        .retryPolicy(retryPolicy)
                        .build();
                cf.start();
                try {
                    DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(cf, "/super", 5);
                    Thread.sleep(1000 * (new Random()).nextInt(3));
                    System.out.println(Thread.currentThread().getName() + "已经准备");
                    barrier.enter();
                    System.out.println("同时开始运行...");
                    Thread.sleep(1000 * (new Random()).nextInt(3));
                    System.out.println(Thread.currentThread().getName() + "运行完毕");
                    barrier.leave();
                    System.out.println("同时退出运行...");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cf.close();
                }



            }, "t" + i).start();
        }
    }

}
