package com.isle.curator.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * Created by 61066 on 2018/1/31.
 */
public class Lock2 {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;//ms

    static CuratorFramework createCuratorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        return cf;
    }

    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch countDown = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                CuratorFramework cf = createCuratorFramework();
                cf.start();
                final InterProcessMutex lock = new InterProcessMutex(cf, "/super");
                try {
                    countDown.await();
                    lock.acquire();
                    System.out.println(Thread.currentThread().getName() + "执行业务逻辑..");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "t" + i).start();
        }
        Thread.sleep(2000);
        countDown.countDown();
    }

}
