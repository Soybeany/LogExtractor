package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.ISFileLoaderData;
import com.soybeany.logextractor.sfile.data.SFileRange;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IStdFileLoaderData extends ISFileLoaderData {

    SFileRange getScanRange();

    void setScanRange(SFileRange range);

    SFileRange getQueryRange();

    void setQueryRange(SFileRange range);

    boolean isReachLoadLimit();

    void setReachLoadLimit(boolean flag);
}
