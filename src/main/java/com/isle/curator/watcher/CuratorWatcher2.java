package com.isle.curator.watcher;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by 61066 on 2018/1/30.
 */
public class CuratorWatcher2 {

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

        //4 建立一个PathChildrenCache缓存,第三个参数为是否接受节点数据内容 如果为false则不接受
        PathChildrenCache cache = new PathChildrenCache(cf, "/super", true);

        //5 在初始化的时候就进行缓存监听
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);


        cache.getListenable().addListener(
                /**
                 * <B>方法名称：</B>监听子节点变更<BR>
                 * <B>概要说明：</B>新建、修改、删除<BR>
                 */
                (client, event) -> {
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
                default:break;
            }
        });

        //创建本身节点不发生变化
        cf.create().forPath("/super", "init".getBytes());

        //添加子节点
        Thread.sleep(1000);
        cf.create().forPath("/super/c1", "c1内容".getBytes());
        Thread.sleep(1000);
        cf.create().forPath("/super/c2", "c2内容".getBytes());

        //修改子节点
        Thread.sleep(1000);
        cf.setData().forPath("/super/c1", "c1更新内容".getBytes());

        //删除子节点
        Thread.sleep(1000);
        cf.delete().forPath("/super/c2");

        //删除本身节点
        Thread.sleep(1000);
        cf.delete().deletingChildrenIfNeeded().forPath("/super");

        Thread.sleep(Integer.MAX_VALUE);

        cf.close();

    }

}
