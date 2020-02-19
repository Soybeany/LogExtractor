package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.ISFileParam;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public abstract class StdParam implements ISFileParam, IStdFileLoaderParam, IStdLogAssemblerParam, IStdReporterParam {

    public static final long DEFAULT_LOAD_SIZE_LIMIT = 100000000;
    public static final int DEFAULT_MAX_LINE_OF_LOG_WITHOUT_START_FLAG = 100;
    public static final int DEFAULT_LOG_LIMIT = 30;

    @Override
    public String getIndexId() {
        return getFileToLoad().toString();
    }

    @Override
    public long getQuerySizeLimit() {
        return DEFAULT_LOAD_SIZE_LIMIT;
    }

    @Override
    public int getMaxLineOfLogWithoutStartFlag() {
        return DEFAULT_MAX_LINE_OF_LOG_WITHOUT_START_FLAG;
    }

    @Override
    public int getLogLimit() {
        return DEFAULT_LOG_LIMIT;
    }
}
