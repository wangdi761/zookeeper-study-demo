package com.isle.curator.cluster;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by 61066 on 2018/1/31.
 */
public class CuratorWatcher {

    /** 父节点path */
    static final String PARENT_PATH = "/super";

    /** zookeeper服务器地址 */
    public static final String CONNECT_ADDR = "192.168.93.134:2181";	/** 定义session失效时间 */

    public static final int SESSION_TIMEOUT = 30000;

    public CuratorWatcher() throws Exception {
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //2 通过工厂创建连接
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                .build();
        //3 建立连接
        cf.start();
        //4 创建跟节点
        if (cf.checkExists().forPath(PARENT_PATH) == null) {
            cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(PARENT_PATH, "super init".getBytes());
            //4 建立一个PathChildrenCache缓存,第三个参数为是否接受节点数据内容 如果为false则不接受
            PathChildrenCache cache = new PathChildrenCache(cf, PARENT_PATH, true);
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            cache.getListenable().addListener((client, event) -> {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED :" + event.getData().getPath());
                        System.out.println("CHILD_ADDED :" + new String(event.getData().getData()));
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED :" + event.getData().getPath());
                        System.out.println("CHILD_UPDATED :" + new String(event.getData().getData()));
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED :" + event.getData().getPath());
                        System.out.println("CHILD_REMOVED :" + new String(event.getData().getData()));
                        break;
                    default:
                        break;
                }
            });
        }
    }

}
