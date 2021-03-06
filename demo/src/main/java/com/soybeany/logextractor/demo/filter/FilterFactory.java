package com.soybeany.logextractor.demo.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.core.query.BaseLogFilterFactory;
import com.soybeany.logextractor.demo.data.Data;
import com.soybeany.logextractor.demo.data.Param;
import com.soybeany.logextractor.sfile.filter.ISFileLogFilterFactory;
import com.soybeany.logextractor.std.data.StdLog;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/19.
 */
public class FilterFactory extends BaseLogFilterFactory<Param, StdLog, Data> implements ISFileLogFilterFactory<StdLog> {
    private Param mParam;
    private List<BaseLogFilter<StdLog>> mFilters;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mParam = param;
    }

    @Override
    public List<? extends BaseLogFilter<StdLog>> getLogFilters() {
        List<BaseLogFilter<StdLog>> list = new LinkedList<BaseLogFilter<StdLog>>();
        addCommonFilters(list);
        return list;
    }

    @Override
    public List<? extends BaseLogFilter<StdLog>> getIncompleteLogFilters() {
        List<BaseLogFilter<StdLog>> list = new LinkedList<BaseLogFilter<StdLog>>();
        if (null != mParam.enableIncompleteLogs) {
            list.add(new IncompleteLogFilter(mParam.enableIncompleteLogs));
        }
        addCommonFilters(list);
        return list;
    }

    @Override
    public void setExtractFilters(List<BaseLogFilter<StdLog>> filters) {
        mFilters = filters;
    }

    private void addCommonFilters(List<BaseLogFilter<StdLog>> list) {
        if (null != mFilters) {
            list.addAll(mFilters);
        }
        if (null != mParam.types) {
            list.add(new TypeFilter(mParam.types));
        }
        if (null != mParam.logId) {
            list.add(new LogIdFilter(mParam.logId));
        }
        if (null != mParam.logContainKey) {
            list.add(new LogContainKeyFilter(mParam.logContainKey));
        }
        if (null != mParam.logContainRegex) {
            list.add(new LogContainRegexFilter(mParam.logContainRegex));
        }
    }

}
