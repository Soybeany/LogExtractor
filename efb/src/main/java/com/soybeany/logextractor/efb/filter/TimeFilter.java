package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.std.data.StdLog;

/**
 * 精确到秒的筛选
 * <br>Created by Soybeany on 2020/2/19.
 */
public class TimeFilter extends BaseLogFilter<StdLog> {
    private Integer mFromValue;
    private Integer mToValue;

    public TimeFilter(Param.Time from, Param.Time to) {
        if (null != from) {
            mFromValue = from.toValue(true);
        }
        if (null != to) {
            mToValue = to.toValue(true);
        }
    }

    @Override
    public boolean isFiltered(StdLog stdLog) {
        if (null != mFromValue) {
            int startValue = Index.getTimeValue(stdLog.startFlag.info.time, true);
            if (mFromValue > startValue) {
                return true;
            }
        }
        if (null != mToValue) {
            int endValue = Index.getTimeValue(stdLog.endFlag.info.time, true);
            return mToValue < endValue;
        }
        return false;
    }
}
