package com.soybeany.logextractor.std.data;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public abstract class StdParam implements IStdParam, IStdFileLoaderParam, IStdLogAssemblerParam, IStdReporterParam {

    public static final long DEFAULT_QUERY_SIZE_LIMIT = 100000000; // 100M
    public static final int DEFAULT_MAX_LINE_OF_LOG_WITHOUT_START_FLAG = 100;
    public static final int DEFAULT_LOG_LIMIT = 30;

    @Override
    public String getIndexId() {
        return getFileToLoad().toString();
    }

    @Override
    public long getQuerySizeLimit() {
        return DEFAULT_QUERY_SIZE_LIMIT;
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
