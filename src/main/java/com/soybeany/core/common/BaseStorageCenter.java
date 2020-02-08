package com.soybeany.core.common;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseStorageCenter<Data> {

    public abstract Data loadData(String dataId) throws DataIdException;

    public abstract void saveData(String dataId, Data data) throws DataIdException;

}
