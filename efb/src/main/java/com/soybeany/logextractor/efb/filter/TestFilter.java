package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.efb.data.flag.RequestFlag;
import com.soybeany.logextractor.efb.util.TypeChecker;
import com.soybeany.logextractor.std.data.StdLog;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class TestFilter extends BaseLogFilter<StdLog> {

    private String mUrl;

    public TestFilter(String url) {
        mUrl = url.toLowerCase();
    }

    @Override
    public boolean isFiltered(StdLog stdLog) {
        if (!TypeChecker.isRequest(stdLog.getType())) {
            return true;
        }
        RequestFlag flag = (RequestFlag) stdLog.startFlag;
        return !flag.url.toLowerCase().contains(mUrl);
    }
}
