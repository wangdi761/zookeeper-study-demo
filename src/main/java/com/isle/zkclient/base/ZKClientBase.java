package com.isle.zkclient.base;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;

/**
 * Created by 61066 on 2018/1/29.
 */
public class ZKClientBase {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.93.134:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;//ms

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR, SESSION_OUTTIME));

        /*zkc.createEphemeral("/temp");
        zkc.createPersistent("/super/c1", true);*/
//        zkc.deleteRecursive("/super");
        /*zkc.createPersistent("/super", "1234");
		zkc.createPersistent("/super/c1", "c1内容");
		zkc.createPersistent("/super/c2", "c2内容");
        List<String> list = zkc.getChildren("/super");
        for (String p : list) {
            System.out.println(p);
			String rp = "/super/" + p;
            String data = zkc.readData(rp);
            System.out.println("节点为：" + rp + "，内容为: " + data);
        }*/
        zkc.writeData("/super/c1", "新内容");
        String data = zkc.readData("/super/c1");
        System.out.println(data);
		System.out.println(zkc.exists("/super/c1"));

        //4.递归删除/super内容
//		zkc.deleteRecursive("/super");
        Thread.sleep(10000);
        zkc.close();
    }

}
