package com.soybeany.core.common;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseStorageCenter<Data, Index> {

    /**
     * 获得源索引，需注意并发修改的问题
     */
    public abstract Index getSourceIndex(Data data);

    /**
     * 获得拷贝索引
     */
    public abstract Index getCopiedIndex(Data data);

    public abstract Data loadData(String dataId) throws DataIdException;

    public abstract void saveData(String dataId, Data data) throws DataIdException;

}
