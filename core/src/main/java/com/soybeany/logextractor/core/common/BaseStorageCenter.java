package com.soybeany.logextractor.core.common;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseStorageCenter<T> {

    public abstract T load(String id);

    public abstract T loadAndSaveIfNotExist(String id, IInstanceFactory<T> factory);

}
