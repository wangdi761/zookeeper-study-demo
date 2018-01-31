package com.isle.zkclient.watcher;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * Created by 61066 on 2018/1/30.
 */
public class ZkClientWatcher2 {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;//ms

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkc = new ZkClient(CONNECT_ADDR, 5000);
        zkc.subscribeDataChanges("/super", new IZkDataListener() {
            @Override
            public void handleDataChange(String path, Object data) throws Exception {
                System.out.println("变更的节点为:" + path + ", 变更内容为:" + data);
            }

            @Override
            public void handleDataDeleted(String path) throws Exception {
                System.out.println("删除的节点为:" + path);
            }
        });

        Thread.sleep(3000);
        zkc.writeData("/super", "456", -1);
        Thread.sleep(1000);

        zkc.delete("/super");
        Thread.sleep(Integer.MAX_VALUE);
    }

}
