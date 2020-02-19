package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.core.query.BaseLogFilterFactory;
import com.soybeany.logextractor.efb.data.Data;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.std.data.StdLog;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/19.
 */
public class FilterFactory extends BaseLogFilterFactory<Param, StdLog, Data> {
    private Param mParam;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mParam = param;
    }

    @Override
    public List<? extends BaseLogFilter<StdLog>> getLogFilters() {
        List<BaseLogFilter<StdLog>> list = new LinkedList<BaseLogFilter<StdLog>>();
        if (null != mParam.fromTime || null != mParam.toTime) {
            list.add(new TimeFilter(mParam.fromTime, mParam.toTime));
        }
        if (null != mParam.url) {
            list.add(new UrlFilter(mParam.url));
        }
        if (null != mParam.userNo) {
            list.add(new UserNoFilter(mParam.userNo));
        }
        return list;
    }

    @Override
    public List<? extends BaseLogFilter<StdLog>> getIncompleteLogFilters() {
        return null;
    }
}
