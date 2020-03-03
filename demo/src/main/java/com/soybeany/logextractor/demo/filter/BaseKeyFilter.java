package com.soybeany.logextractor.demo.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.std.data.StdLog;

/**
 * <br>Created by Soybeany on 2020/2/19.
 */
public abstract class BaseKeyFilter extends BaseLogFilter<StdLog> {

    private String mKey;

    public BaseKeyFilter(String key) {
        mKey = key;
    }

    @Override
    public boolean isFiltered(StdLog stdLog) {
        if (!isTypeSuitable((stdLog.getType()))) {
            return true;
        }
        return !getSource(stdLog).contains(mKey);
    }

    protected abstract boolean isTypeSuitable(String type);

    protected abstract String getSource(StdLog stdLog);
}
