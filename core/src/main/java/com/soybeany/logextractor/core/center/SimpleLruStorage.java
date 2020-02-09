package com.soybeany.logextractor.core.center;

import com.soybeany.logextractor.core.common.BusinessException;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class SimpleLruStorage<Key, Value> {
    private final Map<Key, Value> mMap = new LinkedHashMap<Key, Value>(0, 0.75f, true);

    private int mCapacity = 10;

    public synchronized void setCapacity(int count) {
        mCapacity = count;
    }

    @SuppressWarnings("SameParameterValue")
    public synchronized Value getAndCheck(Key key, boolean checkValue) {
        Value value = get(key);
        if (null == value) {
            if (!mMap.containsKey(key)) {
                throw new BusinessException("指定的id不存在");
            }
            if (checkValue) {
                throw new BusinessException("指定的id没有找到数据");
            }
        }
        return value;
    }

    public synchronized void putAndCheck(Key key, Value value) {
        trimSize();
        if (mMap.containsKey(key)) {
            throw new BusinessException("指定id的数据已存在");
        }
        mMap.put(key, value);
    }

    public synchronized Value get(Key key) {
        return mMap.get(key);
    }

    public synchronized void put(Key key, Value value) {
        trimSize();
        mMap.put(key, value);
    }

    public synchronized void clear() {
        mMap.clear();
    }

    private void trimSize() {
        if (mMap.isEmpty()) {
            return;
        }
        Set<Key> keySet = mMap.keySet();
        Iterator<Key> iterator = keySet.iterator();

        while (keySet.size() >= mCapacity) {
            mMap.remove(iterator.next());
        }
    }
}
