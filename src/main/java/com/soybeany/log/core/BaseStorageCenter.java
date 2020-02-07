package com.soybeany.log.core;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseStorageCenter<Data, Report> {

    public abstract Data loadData(String dataId) throws DataIdException;

    public abstract void saveData(String dataId, Data data) throws DataIdException;

    public abstract Report loadReport(String dataId) throws DataIdException;

    public abstract void saveReport(String dataId, Report report) throws DataIdException;
}
