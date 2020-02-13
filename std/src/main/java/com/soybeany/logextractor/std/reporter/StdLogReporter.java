package com.soybeany.logextractor.std.reporter;

import com.soybeany.logextractor.core.query.BaseLogReporter;
import com.soybeany.logextractor.sfile.data.IRenewalInfoAccessor;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.ILoadDataAccessor;
import com.soybeany.logextractor.std.data.IStdReporterParam;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.StdReport;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdLogReporter<Param extends IStdReporterParam, Data extends ILoadDataAccessor & IRenewalInfoAccessor> extends BaseLogReporter<Param, StdLog, StdReport, Data> {

    private Data mData;
    private int mLogLimit;
    private List<StdLog> mLogs;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mData = data;
        mLogLimit = param.getLogLimit();
        mLogs = new LinkedList<StdLog>();
    }

    @Override
    public boolean needMoreLog() {
        return mLogs.size() < mLogLimit;
    }

    @Override
    public void addLog(StdLog log) {
        mLogs.add(log);
    }

    @Override
    public StdReport getNewReport() {
        StdReport report = new StdReport();
        report.logs = mLogs;
        report.expectCount = mLogLimit;
        report.actualCount = mLogs.size();
        report.endReason = getEndReason(!needMoreLog(), !mData.canQueryMore());
        report.lastDataId = mData.getLastDataId();
        report.curDataId = mData.getCurDataId();
        report.nextDataId = mData.getNextDataId();
        SFileRange queryRange = mData.getQueryRange();
        if (null != queryRange) {
            report.startPointer = queryRange.start;
            report.endPointer = queryRange.end;
        }
        SFileRange scanRange = mData.getScanRange();
        if (null != scanRange) {
            report.totalScan = scanRange.end;
            report.newScan = scanRange.end - scanRange.start;
        }
        return report;
    }

    private String getEndReason(boolean hasEnoughLog, boolean isLoadToEnd) {
        if (hasEnoughLog) {
            return "已找到指定数量的结果";
        }
        if (isLoadToEnd) {
            return "已到达文件的末尾";
        }
        return "已到达限制区域的末尾";
    }

}
