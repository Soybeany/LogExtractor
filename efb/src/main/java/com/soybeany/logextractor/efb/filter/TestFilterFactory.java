package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.core.query.BaseLogFilterFactory;
import com.soybeany.logextractor.efb.data.Data;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.std.data.StdLog;

import java.util.Collections;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class TestFilterFactory extends BaseLogFilterFactory<Param, StdLog, Data> {
    private Param mParam;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mParam = param;
    }

    @Override
    public List<? extends BaseLogFilter<StdLog>> getLogFilters() {
        if (null != mParam.url) {
            return Collections.singletonList(new TestFilter(mParam.url));
        }
        return null;
    }

    @Override
    public List<? extends BaseLogFilter<StdLog>> getIncompleteLogFilters() {
        return null;
    }
}
