package com.isle.zookeeper.cluster;

import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 61066 on 2018/1/29.
 */
public class ZKWatcher implements Watcher {

    /** zk变量 */
    private ZooKeeper zk = null;

    /** 父节点path */
    static final String PARENT_PATH = "/super";

    /** 信号量设置，用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行 */
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private List<String> cowaList = new ArrayList<>();

    /** zookeeper服务器地址 */
    public static final String CONNECTION_ADDR = "192.168.93.134:2181";
    /** 定义session失效时间 */
    public static final int SESSION_TIMEOUT = 30000;

    public ZKWatcher() throws Exception {
        zk = new ZooKeeper(CONNECTION_ADDR, SESSION_TIMEOUT, this);
        System.out.println("开始连接ZK服务器");
        connectedSemaphore.await();
    }

    @Override
    public void process(WatchedEvent event) {
        // 连接状态
        Event.KeeperState keeperState = event.getState();
        // 事件类型
        Event.EventType eventType = event.getType();
        // 受影响的path
        String path = event.getPath();
        System.out.println("受影响的path : " + path);

        if (Event.KeeperState.SyncConnected.equals(keeperState)) {
            if (Event.EventType.None.equals(eventType)) {
                System.out.println("成功连接上ZK服务器");
                connectedSemaphore.countDown();
                try {
                    if(zk.exists(PARENT_PATH, false) == null){
                        zk.create(PARENT_PATH, "root".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    List<String> paths = zk.getChildren(PARENT_PATH, true);
                    for (String p : paths) {
                        System.out.println(p);
                        zk.exists(PARENT_PATH + "/" + p, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (Event.EventType.NodeCreated == eventType) {
                System.out.println("节点创建");
                try {
                    zk.exists(path, true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (Event.EventType.NodeDataChanged == eventType) {
                System.out.println("节点数据更新");
                try {
                    //update nodes  call function
                    this.zk.exists(path, true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }else if (Event.EventType.NodeChildrenChanged == eventType) {
                System.out.println("子节点 ... 变更");
                try {
                    List<String> paths = this.zk.getChildren(path, true);
                    if(paths.size() >= cowaList.size()){
                        paths.removeAll(cowaList);
                        for(String p : paths){
                            this.zk.exists(path + "/" + p, true);
                            //this.zk.getChildren(path + "/" + p, true);
                            System.out.println("这个是新增的子节点 : " + path + "/" + p);
                            //add new nodes  call function
                        }
                        cowaList.addAll(paths);
                    } else {
                        cowaList = paths;
                    }
                    System.out.println("cowaList: " + cowaList.toString());
                    System.out.println("paths: " + paths.toString());

                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //删除节点
            else if (Event.EventType.NodeDeleted == eventType) {
                System.out.println("节点 " + path + " 被删除");
                try {
                    //delete nodes  call function
                    this.zk.exists(path, true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (Event.KeeperState.Disconnected == keeperState) {
            System.out.println("与ZK服务器断开连接");
        }
        else if (Event.KeeperState.AuthFailed == keeperState) {
            System.out.println("权限检查失败");
        }
        else if (Event.KeeperState.Expired == keeperState) {
            System.out.println("会话失效");
        }
        else ;

        System.out.println("--------------------------------------------");

    }
}
