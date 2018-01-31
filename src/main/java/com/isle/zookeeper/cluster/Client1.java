package com.isle.zookeeper.cluster;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by 61066 on 2018/1/29.
 */
public class Client1 {


    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 2000;//ms
    /** 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号 */
    static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception{

        ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                //获取事件的状态
                Event.KeeperState keeperState = event.getState();
                Event.EventType eventType = event.getType();
                //如果是建立连接
                if(Event.KeeperState.SyncConnected == keeperState){
                    if(Event.EventType.None == eventType){
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        System.out.println("zk 建立连接");
                    }
                }
            }
        });

        //进行阻塞
        connectedSemaphore.await();

		//创建子节点
		zk.create("/super/c1", "c1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //创建子节点
		zk.create("/super/c2", "c2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //创建子节点
        zk.create("/super/c3", "c3".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //创建子节点
		zk.create("/super/c4", "c4".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

		zk.create("/super/c4/c44", "c44".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //获取节点信息
		byte[] data = zk.getData("/testRoot", false, null);
	    System.out.println(new String(data));
		System.out.println(zk.getChildren("/testRoot", false));

        //修改节点的值
		zk.setData("/super/c1", "modify c1".getBytes(), -1);
		zk.setData("/super/c2", "modify c2".getBytes(), -1);
		data = zk.getData("/super/c2", false, null);
		System.out.println(new String(data));

//		//判断节点是否存在
		System.out.println(zk.exists("/super/c3", false));
		//删除节点
		zk.delete("/super/c3", -1);








        zk.close();



    }

}
