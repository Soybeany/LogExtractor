package com.soybeany.logextractor.core.center;

import com.soybeany.logextractor.core.common.BusinessException;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class SimpleUniqueLock {

    private static final Map<Object, String> SET = new HashMap<Object, String>();

    public static synchronized void tryAttain(String id, Object obj, String msg) {
        if (null == id) {
            throw new BusinessException("SimpleUniqueLock的id不能为null");
        }
        if (SET.containsKey(obj)) {
            throw new BusinessException(msg);
        }
        SET.put(obj, id);
    }

    public static synchronized void release(String id, Object obj) {
        if (null == id) {
            return;
        }
        if (id.equals(SET.get(obj))) {
            SET.remove(obj);
        }
    }
}
