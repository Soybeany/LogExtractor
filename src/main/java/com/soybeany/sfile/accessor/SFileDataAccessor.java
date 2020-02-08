package com.soybeany.sfile.accessor;

import com.soybeany.core.query.BaseDataAccessor;

/**
 * <br>Created by Soybeany on 2020/2/8.
 */
public abstract class SFileDataAccessor<Data, Index, Report> extends BaseDataAccessor<Data, Index, Report> {

    public abstract String getLastDataId(Data data);

    public abstract void setLastDataId(Data data, String id);

    public abstract String getNextDataId(Data data);

    public abstract void setNextDataId(Data data, String id);

    public abstract long getPointer(Data data);

    public abstract void setPointer(Data data, long pointer);

    public abstract Data getNewData(Data source);

}
