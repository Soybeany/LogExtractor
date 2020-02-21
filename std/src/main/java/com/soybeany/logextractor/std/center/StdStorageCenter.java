package com.soybeany.logextractor.std.center;

import com.soybeany.logextractor.core.center.MemStorageCenter;

/**
 * <br>Created by Soybeany on 2020/2/21.
 */
public class StdStorageCenter<T> extends MemStorageCenter<T> implements IStdStorageCenter {
    @Override
    public void remove(String id) {
        STORAGE.remove(id);
    }
}
