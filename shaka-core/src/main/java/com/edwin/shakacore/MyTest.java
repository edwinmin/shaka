package com.edwin.shakacore;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jinming.wu
 * @date 2015-5-29
 */
public class MyTest {

    private Lock      lock      = new ReentrantLock();

    private Condition condition = lock.newCondition();

    class NewTest {

    }

    public void signal() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        try {
            lock.lock();
            condition.signal();
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }

    public static void main(String args[]) throws Exception {

        final MyTest test = new MyTest();

        test.lock.lock();

        try {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    test.signal();
                }
            });
            thread.start();
            if (test.condition.await(3, TimeUnit.SECONDS)) {
                System.out.println("test");
            } else {
                System.out.println("aa");
            }
            System.out.println("continue");
        } finally {
            System.out.println("unlock");
            test.lock.unlock();
        }
    }
}
