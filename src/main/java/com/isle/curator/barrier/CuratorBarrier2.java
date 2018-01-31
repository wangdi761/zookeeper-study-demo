package com.isle.curator.barrier;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by 61066 on 2018/1/31.
 */
public class CuratorBarrier2 {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;//ms

    static DistributedBarrier barrier = null;

    public static void main(String[] args) throws Exception {
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
                    barrier = new DistributedBarrier(cf, "/super");
                    System.out.println(Thread.currentThread().getName() + "设置barrier!");
                    barrier.setBarrier();
                    barrier.waitOnBarrier();
                    System.out.println("---------开始执行程序----------");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cf.close();
                }
            }, "t" + i).start();
        }
        Thread.sleep(10000);
        barrier.removeBarrier();
    }

}
