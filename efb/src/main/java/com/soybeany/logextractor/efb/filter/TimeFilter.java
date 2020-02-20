package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.std.data.StdLog;

/**
 * 精确到秒的筛选
 * <br>Created by Soybeany on 2020/2/19.
 */
public class TimeFilter extends BaseLogFilter<StdLog> {
    protected Integer mFromValue;
    protected Integer mToValue;

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
        int timeValue = Index.getTimeValue(getTime(stdLog), true);
        // 时间点在指定开始时间前
        if (null != mFromValue && mFromValue > timeValue) {
            return true;
        }
        // 时间点在指定结束时间后
        return null != mToValue && mToValue < timeValue;
    }

    private String getTime(StdLog stdLog) {
        if (null != stdLog.startFlag) {
            return stdLog.startFlag.info.time;
        }
        if (!stdLog.lines.isEmpty()) {
            return stdLog.lines.getFirst().info.time;
        }
        if (null != stdLog.endFlag) {
            return stdLog.endFlag.info.time;
        }
        throw new BusinessException("存在未预料的全空日志");
    }
}
