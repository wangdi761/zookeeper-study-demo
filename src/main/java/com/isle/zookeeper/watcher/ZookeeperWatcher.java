package com.isle.zookeeper.watcher;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 61066 on 2018/1/26.
 */
public class ZookeeperWatcher implements Watcher {

    /** 定义原子变量 */
    AtomicInteger seq = new AtomicInteger();
    /** 定义session失效时间 */
    private static final int SESSION_TIMEOUT = 10000;
    /** zookeeper服务器地址 */
    private static final String CONNECTION_ADDR = "192.168.93.134:2181,192.168.93.134:2182,192.168.93.134:2183";
    /** zk父路径设置 */
    private static final String PARENT_PATH = "/p";
    /** zk子路径设置 */
    private static final String CHILDREN_PATH = "/p/c";
    /** 进入标识 */
    private static final String LOG_PREFIX_OF_MAIN = "【Main】";
    /** zk变量 */
    private ZooKeeper zk = null;
    /**用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行 */
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    /**
     * 创建ZK连接
     * @param connectAddr ZK服务器地址列表
     * @param sessionTimeout Session超时时间
     */
    public void createConnection(String connectAddr, int sessionTimeout) {
        releaseConnection();
        try {
            zk = new ZooKeeper(connectAddr, sessionTimeout, this);
            System.out.println(LOG_PREFIX_OF_MAIN + "开始连接ZK服务器");
            connectedSemaphore.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭ZK连接
     */
    public void releaseConnection() {
        if (this.zk != null) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建节点
     * @param path 节点路径
     * @param data 数据内容
     * @return
     */
    public boolean createPath(String path, String data, boolean needWatch) {
        try {
            zk.exists(path, needWatch);
            System.out.println(LOG_PREFIX_OF_MAIN + "节点创建成功, Path: " +
                            zk.create(path, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
                            + ", content: " + data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取指定节点数据内容
     * @param path 节点路径
     * @return
     */
    public String readData(String path, boolean needWatch) {
        System.out.println("读取数据操作...");
        try {
            return new String(zk.getData(path, needWatch,null));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新指定节点数据内容
     * @param path 节点路径
     * @param data 数据内容
     * @return
     */
    public boolean writeData(String path, String data) {
        try {
            System.out.println(LOG_PREFIX_OF_MAIN + "更新数据成功，path：" + path + ", stat: "
                                + zk.setData(path, data.getBytes(), -1));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断指定节点是否存在
     * @param path 节点路径
     */
    public Stat exists(String path, boolean needWatch) {
        try {
            return this.zk.exists(path, needWatch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取子节点
     * @param path 节点路径
     */
    private List<String> getChildren(String path, boolean needWatch) {
        try {
            System.out.println("读取子节点操作...");
            return zk.getChildren(path, needWatch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除指定节点
     *
     * @param path
     *            节点path
     */
    public void deleteNode(String path) {
        try {
            this.zk.delete(path, -1);
            System.out.println(LOG_PREFIX_OF_MAIN + "删除节点成功，path：" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有节点
     */
    private void deleteAllTestPath(boolean needWatch) {
        if (exists(CHILDREN_PATH, needWatch) != null) {
            deleteNode(CHILDREN_PATH);
        }
        if(this.exists(PARENT_PATH, needWatch) != null){
            this.deleteNode(PARENT_PATH);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("进入 process 。。。。。event = " + event);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (event == null) {
            return;
        }

        KeeperState keeperState = event.getState();
        EventType eventType = event.getType();
        String path = event.getPath();

        String logPrefix = "【Watcher-" + seq.incrementAndGet() + "】";

        System.out.println(logPrefix + "收到Watcher通知");
        System.out.println(logPrefix + "连接状态:\t" + keeperState.toString());
        System.out.println(logPrefix + "事件类型:\t" + eventType.toString());

        if (KeeperState.SyncConnected.equals(keeperState)) {
            if (EventType.None.equals(eventType)) {
                System.out.println(logPrefix + "成功连接上ZK服务器");
                connectedSemaphore.countDown();
            } else if (EventType.NodeCreated.equals(eventType)) {
                System.out.println(logPrefix + "节点创建");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (EventType.NodeDataChanged.equals(eventType)) {
                System.out.println(logPrefix + "节点数据更新");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (EventType.NodeChildrenChanged.equals(eventType)) {
                System.out.println(logPrefix + "子节点变更");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (EventType.NodeDeleted.equals(eventType)) {
                System.out.println(logPrefix + "节点 " + path + " 被删除");
            }
        } else if (KeeperState.Disconnected.equals(keeperState)) {
            System.out.println(logPrefix + "与ZK服务器断开连接");
        } else if (KeeperState.AuthFailed.equals(keeperState)) {
            System.out.println(logPrefix + "权限检查失败");
        } else if (KeeperState.Expired.equals(keeperState)) {
            System.out.println(logPrefix + "会话失效");
        }
        System.out.println("--------------------------------------------");
    }

    public static void main(String[] args) throws InterruptedException {
        //建立watcher //当前客户端可以称为一个watcher 观察者角色
        ZookeeperWatcher zkWatcher = new ZookeeperWatcher();
        //创建连接
        zkWatcher.createConnection(CONNECTION_ADDR, SESSION_TIMEOUT);

        Thread.sleep(1000);
        if (zkWatcher.createPath(PARENT_PATH, System.currentTimeMillis() + "", true)) {
            Thread.sleep(1000);

            zkWatcher.readData(PARENT_PATH, true);

            zkWatcher.getChildren(PARENT_PATH, true);

            zkWatcher.writeData(PARENT_PATH, System.currentTimeMillis() + "");

            // 创建子节点
            zkWatcher.createPath(CHILDREN_PATH, System.currentTimeMillis() + "", true);


            /*zkWatcher.createPath(CHILDREN_PATH + "/c1", System.currentTimeMillis() + "", true);
			zkWatcher.createPath(CHILDREN_PATH + "/c1/c2", System.currentTimeMillis() + "", true);*/

            zkWatcher.deleteAllTestPath(true);

        }



        Thread.sleep(10000);
        zkWatcher.releaseConnection();
    }

}
