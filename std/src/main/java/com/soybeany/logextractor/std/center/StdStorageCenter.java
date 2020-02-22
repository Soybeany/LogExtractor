package com.soybeany.logextractor.std.center;

import com.soybeany.logextractor.core.center.MemStorageCenter;
import com.soybeany.logextractor.core.tool.SimpleLruStorage;

/**
 * <br>Created by Soybeany on 2020/2/21.
 */
public class StdStorageCenter<T> extends MemStorageCenter<T> implements IStdStorageCenter, SimpleLruStorage.IListener<String, Object> {

    {
        STORAGE.addListener(this);
    }

    @Override
    public void remove(String id) {
        STORAGE.remove(id);
    }

    @Override
    public void onTrim(String id, Object o) {
        StdAutoRemoveTaskCenter.removeTask(id);
    }
}
