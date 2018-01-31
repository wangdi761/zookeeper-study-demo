package com.isle.curator.automicinteger;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

/**
 * Created by 61066 on 2018/1/31.
 */
public class CuratorAtomicInteger {

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
        cf.start();
        DistributedAtomicInteger atomicInteger =
                new DistributedAtomicInteger(
                cf, "/super",
                        new RetryNTimes(3, 1000));
        AtomicValue<Integer> value = atomicInteger.increment();
        System.out.println(value.succeeded());
        System.out.println(value.postValue());	//最新值
        System.out.println(value.preValue());	//原始值
        cf.close();
    }


}
