package com.isle.curator.lock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 61066 on 2018/1/31.
 */
public class Lock1 {

    static ReentrantLock lock = new ReentrantLock();

    static int count = 10;

    public static void genarNo() {
        try {
            lock.lock();
            count --;
            System.out.println(count);
        } finally {
            lock.unlock();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    genarNo();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }, "t" + i).start();
        }
        Thread.sleep(50);
        countDownLatch.countDown();
    }

}
