package com.soybeany.logextractor.std.center;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <br>Created by Soybeany on 2020/2/22.
 */
public class StdAutoRemoveTaskCenter {

    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, Future<?>> TASK_MAP = new HashMap<String, Future<?>>();

    public static synchronized void addTask(String id, int delayInSec, Runnable runnable) {
        Future<?> future = TASK_MAP.put(id, SERVICE.schedule(runnable, delayInSec, TimeUnit.SECONDS));
        cancelFuture(future);
    }

    public static synchronized void removeTask(String id) {
        Future<?> future = TASK_MAP.remove(id);
        cancelFuture(future);
    }

    private static void cancelFuture(Future<?> future) {
        if (null != future) {
            future.cancel(false);
        }
    }
}
