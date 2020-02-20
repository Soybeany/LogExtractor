package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.std.data.StdLog;

/**
 * <br>Created by Soybeany on 2020/2/20.
 */
public class LogIdFilter extends BaseKeyIgnoreCaseFilter {

    public LogIdFilter(String key) {
        super(key);
    }

    @Override
    protected String getSource2(StdLog stdLog) {
        return stdLog.logId;
    }

    @Override
    protected boolean isTypeSuitable(String type) {
        return true;
    }
}
