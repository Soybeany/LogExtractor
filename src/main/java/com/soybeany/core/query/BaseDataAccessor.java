package com.soybeany.core.query;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public abstract class BaseDataAccessor<Data, Index, Report> {

    public abstract String getCurDataId(Data data);

    public abstract void setIndex(Data data, Index index);

    public abstract Index getIndex(Data data);

    public abstract void setReport(Data data, Report report);

    public abstract Report getReport(Data data);

}
