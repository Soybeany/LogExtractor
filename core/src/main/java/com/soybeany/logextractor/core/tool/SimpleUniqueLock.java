package com.soybeany.logextractor.core.tool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class SimpleUniqueLock {

    public static void tryAttain(Lock lock, int timeoutSec) throws InterruptedException {
        if (!lock.tryLock(timeoutSec, TimeUnit.SECONDS)) {
            throw new InterruptedException("在指定的超时内无法获取锁");
        }
    }

    /**
     * @return 锁是否释放成功
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean release(Lock lock) {
        try {
            lock.unlock();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
