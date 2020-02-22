package com.soybeany.logextractor.core.tool;

import com.soybeany.logextractor.core.common.BusinessException;

import java.util.*;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class SimpleLruStorage<Key, Value> {
    private final Map<Key, Value> mMap = new LinkedHashMap<Key, Value>(0, 0.75f, true);

    private List<IListener<Key, Value>> mListeners = new LinkedList<IListener<Key, Value>>();
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

    @SuppressWarnings("UnusedReturnValue")
    public synchronized Value remove(Key key) {
        return mMap.remove(key);
    }

    public synchronized void clear() {
        mMap.clear();
    }

    public synchronized void addListener(IListener<Key, Value> listener) {
        mListeners.add(listener);
    }

    public synchronized void removeListener(IListener<Key, Value> listener) {
        mListeners.remove(listener);
    }

    private void trimSize() {
        if (mMap.isEmpty()) {
            return;
        }
        Set<Key> keySet = mMap.keySet();
        Iterator<Key> iterator = keySet.iterator();

        while (keySet.size() >= mCapacity) {
            Key key = iterator.next();
            Value value = mMap.remove(key);
            for (IListener<Key, Value> listener : mListeners) {
                listener.onTrim(key, value);
            }
        }
    }

    public interface IListener<Key, Value> {
        void onTrim(Key key, Value value);
    }
}
