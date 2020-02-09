package com.soybeany.logextractor.sfile.data;

import com.soybeany.logextractor.core.data.IDataIdAccessor;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IRenewalInfoAccessor extends IDataIdAccessor {

    String getLastDataId();

    void setLastDataId(String id);

    String getNextDataId();

    void setNextDataId(String id);

    long getPointer();

    void setPointer(long pointer);

}
