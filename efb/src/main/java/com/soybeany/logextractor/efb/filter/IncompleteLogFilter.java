package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.std.data.StdLog;

/**
 * <br>Created by Soybeany on 2020/2/20.
 */
public class IncompleteLogFilter extends BaseLogFilter<StdLog> {

    private boolean mEnable;

    public IncompleteLogFilter(boolean enable) {
        mEnable = enable;
    }

    @Override
    public boolean isFiltered(StdLog stdLog) {
        return !mEnable;
    }

}
