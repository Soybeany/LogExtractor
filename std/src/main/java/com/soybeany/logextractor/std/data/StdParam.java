package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.ISFileParam;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public abstract class StdParam implements ISFileParam, IStdFileLoaderParam, IStdLogAssemblerParam, IStdReporterParam {

    @Override
    public String getIndexId() {
        return getFileToLoad().toString();
    }

    @Override
    public long getLoadSizeLimit() {
        return 100000000;
    }

    @Override
    public int getMaxLineOfLogWithoutStartFlag() {
        return 100;
    }

    @Override
    public int getLogLimit() {
        return 30;
    }
}
