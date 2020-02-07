package com.soybeany.core.query;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public abstract class BaseDataAccessor<Data> {

    public abstract String getCurDataId(Data data);

    public abstract void setNextDataId(Data data, String dataId);

    public abstract Data getNextData(Data data);
}
