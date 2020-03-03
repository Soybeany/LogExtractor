package com.soybeany.logextractor.demo.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;

import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/20.
 */
public class LogContainRegexFilter extends BaseLogFilter<StdLog> {
    private Pattern mPattern;

    public LogContainRegexFilter(String key) {
        mPattern = Pattern.compile(key);
    }

    @Override
    public boolean isFiltered(StdLog stdLog) {
        for (StdLine line : stdLog.lines) {
            if (mPattern.matcher(line.content).find()) {
                return false;
            }
        }
        return true;
    }
}
