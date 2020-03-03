package com.soybeany.logextractor.demo.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;

/**
 * <br>Created by Soybeany on 2020/2/20.
 */
public class LogContainKeyFilter extends BaseLogFilter<StdLog> {
    private String mKey;

    public LogContainKeyFilter(String key) {
        mKey = key.toLowerCase();
    }

    @Override
    public boolean isFiltered(StdLog stdLog) {
        for (StdLine line : stdLog.lines) {
            if (line.content.toLowerCase().contains(mKey)) {
                return false;
            }
        }
        return true;
    }
}
