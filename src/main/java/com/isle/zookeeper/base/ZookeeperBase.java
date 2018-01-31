package com.isle.zookeeper.base;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by 61066 on 2018/1/25.
 */
public class ZookeeperBase {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181,192.168.93.134:2182,192.168.93.134:2183";

    /** session超时时间 */
    static final int SESSION_OUTTIME = 2000;//ms

    /** 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号 */
    static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //获取事件的状态
                Event.KeeperState keeperState = event.getState();
                //获取事件的类型
                Event.EventType eventType = event.getType();
                if (Event.KeeperState.SyncConnected == keeperState) {
                    if (Event.EventType.None == eventType) {
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        System.out.println("zk 建立连接");
                    }
                }
            }
        });
        //进行阻塞
        connectedSemaphore.await();

        System.out.println("..");

        //创建父节点
        /*String result = zk.create("/testRoot", "/testRoot1111".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(result);*/

        //创建子节点
//        zk.create("/testRoot/children", "children data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //删除节点
        /*zk.delete("/testRoot", -1, new VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                System.out.println(rc);
                System.out.println(path);
                System.out.println(ctx);
            }
        }, "a");*/
        //获取子节点（不能递归获取）
        /*List<String> list = zk.getChildren("/testRoot", false);
        for (String path : list) {
            System.out.println(path);
            String realPath = "/testRoot/" + path;
            System.out.println(new String(zk.getData(realPath, false, null)));
        }*/
        /*zk.setData("/testRoot", "modify data".getBytes(),-1);
        byte[] data = zk.getData("/testRoot", false, null);
        System.out.println(new String(data));*/
        System.out.println(zk.exists("/testRoot/11", false));
        zk.close();
    }


}
