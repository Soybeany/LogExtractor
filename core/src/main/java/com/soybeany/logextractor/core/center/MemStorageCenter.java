package com.soybeany.logextractor.core.center;

import com.soybeany.logextractor.core.common.BaseStorageCenter;
import com.soybeany.logextractor.core.common.IInstanceFactory;
import com.soybeany.logextractor.core.tool.SimpleLruStorage;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class MemStorageCenter<T> extends BaseStorageCenter<T> {

    protected static final SimpleLruStorage<String, Object> STORAGE = new SimpleLruStorage<String, Object>();

    @Override
    @SuppressWarnings("unchecked")
    public T load(String id) {
        return (T) STORAGE.getAndCheck(id, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T loadAndSaveIfNotExist(String id, IInstanceFactory<T> factory) {
        synchronized (STORAGE) {
            T data = (T) STORAGE.get(id);
            if (null == data) {
                STORAGE.put(id, data = factory.getNew());
            }
            return data;
        }
    }

}
