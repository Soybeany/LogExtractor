package com.soybeany.core.impl.center;

import com.soybeany.core.common.ConcurrencyException;

import java.util.HashSet;
import java.util.Set;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class SimpleUniqueLock {

    private static final Set<Object> SET = new HashSet<Object>();

    public static synchronized void tryAttain(Object obj, String msg) throws ConcurrencyException {
        if (SET.contains(obj)) {
            throw new ConcurrencyException(msg);
        }
        SET.add(obj);
    }

    public static synchronized void release(Object obj) {
        SET.remove(obj);
    }

}
