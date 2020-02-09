package com.soybeany.logextractor.std.reporter;

import com.soybeany.logextractor.core.query.BaseQueryReporter;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.IReportInfo;
import com.soybeany.logextractor.std.data.Log;
import com.soybeany.logextractor.std.data.QueryReport;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdQueryReporter<Data extends IReportInfo> extends BaseQueryReporter<Log, QueryReport, Data> {

    private Data mData;
    private int mLogLimit;
    private List<Log> mLogs;

    @Override
    public void onStart(Data data) throws Exception {
        super.onStart(data);
        mData = data;
        mLogLimit = data.getLogLimit();
        mLogs = new LinkedList<Log>();
    }

    @Override
    public boolean needMoreLog() {
        return mLogs.size() < mLogLimit;
    }

    @Override
    public void addLog(Log log) {
        mLogs.add(log);
    }

    @Override
    public QueryReport getNewReport() {
        QueryReport report = new QueryReport();
        report.logs = mLogs;
        report.expectCount = mLogLimit;
        report.actualCount = mLogs.size();
        report.endReason = getEndReason(!needMoreLog(), !mData.canQueryMore());
        report.lastDataId = mData.getLastDataId();
        report.curDataId = mData.getCurDataId();
        report.nextDataId = mData.getNextDataId();
        SFileRange queryRange = mData.getQueryRange();
        report.startPointer = queryRange.start;
        report.endPointer = queryRange.end;
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
