package com.soybeany.sfile.center;

import com.soybeany.core.common.DataIdException;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class SimpleLruStorage {
    private final Map<String, Object> mMap = new LinkedHashMap<String, Object>(0, 0.75f, true);

    private int mCapacity = 10;

    synchronized void setCapacity(int count) {
        mCapacity = count;
    }

    synchronized <T> T getAndCheck(String key, boolean checkValue) throws DataIdException {
        T value = get(key);
        if (null == value) {
            if (!mMap.containsKey(key)) {
                throw new DataIdException("指定的id不存在");
            }
            if (checkValue) {
                throw new DataIdException("指定的id没有找到数据");
            }
        }
        return value;
    }

    synchronized void putAndCheck(String key, Object value) throws DataIdException {
        trimSize();
        if (mMap.containsKey(key)) {
            throw new DataIdException("指定id的数据已存在");
        }
        mMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    synchronized <T> T get(String key) {
        return (T) mMap.get(key);
    }

    synchronized void put(String key, Object value) {
        trimSize();
        mMap.put(key, value);
    }

    synchronized void clear() {
        mMap.clear();
    }

    private void trimSize() {
        if (mMap.isEmpty()) {
            return;
        }
        Set<String> keySet = mMap.keySet();
        Iterator<String> iterator = keySet.iterator();

        while (keySet.size() >= mCapacity) {
            mMap.remove(iterator.next());
        }
    }
}
