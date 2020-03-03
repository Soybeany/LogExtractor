package com.soybeany.logextractor.demo.filter;

import com.soybeany.logextractor.std.data.StdLog;

/**
 * <br>Created by Soybeany on 2020/2/19.
 */
public abstract class BaseKeyIgnoreCaseFilter extends BaseKeyFilter {

    public BaseKeyIgnoreCaseFilter(String key) {
        super(key.toLowerCase());
    }

    @Override
    protected String getSource(StdLog stdLog) {
        return getSource2(stdLog).toLowerCase();
    }

    protected abstract String getSource2(StdLog stdLog);
}
