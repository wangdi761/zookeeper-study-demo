package com.isle.zkclient.watcher;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;

/**
 * Created by 61066 on 2018/1/30.
 */
public class ZkClientWatcher1 {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;//ms

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkc = new ZkClient(CONNECT_ADDR, 5000);
        zkc.subscribeChildChanges("/super", (parentPath, currentChilds) -> {
            System.out.println("parentPath: " + parentPath);
            System.out.println("currentChilds: " + currentChilds);
        });

        Thread.sleep(1000);

        zkc.createPersistent("/super");
        Thread.sleep(1000);

        zkc.createPersistent("/super" + "/" + "c1", "c1内容");
        Thread.sleep(1000);

        zkc.createPersistent("/super" + "/" + "c2", "c2内容");
        Thread.sleep(1000);

        zkc.delete("/super/c2");
        Thread.sleep(1000);

        zkc.deleteRecursive("/super");
        Thread.sleep(Integer.MAX_VALUE);

    }
}
