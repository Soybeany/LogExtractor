package com.soybeany.logextractor.core.center;

import com.soybeany.logextractor.core.common.BaseStorageCenter;
import com.soybeany.logextractor.core.common.IInstanceFactory;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class MemStorageCenter<T> extends BaseStorageCenter<T> {

    private static final SimpleLruStorage<String, Object> STORAGE = new SimpleLruStorage<String, Object>();

    @Override
    @SuppressWarnings("unchecked")
    public T load(String id) {
        return (T) STORAGE.getAndCheck(id, true);
    }

    @Override
    public void save(String id, T data) {
        STORAGE.putAndCheck(id, data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized T loadAndSaveIfNotExist(String id, IInstanceFactory<T> factory) {
        T data = (T) STORAGE.get(id);
        if (null == data) {
            STORAGE.put(id, data = factory.getNew());
        }
        return data;
    }

}
