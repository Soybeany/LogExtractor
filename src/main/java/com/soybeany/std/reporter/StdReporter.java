package com.soybeany.std.reporter;

import com.soybeany.core.query.BaseReporter;
import com.soybeany.sfile.data.ISFileData;
import com.soybeany.sfile.loader.SFileRange;
import com.soybeany.std.data.IReportData;
import com.soybeany.std.data.Log;
import com.soybeany.std.data.Report;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdReporter<Data extends ISFileData & IReportData> extends BaseReporter<Data, Log, Report> {

    private Data mData;
    private List<Log> mLogs;
    private int mLimitCount;

    @Override
    public void onInit(Data data) {
        super.onInit(data);
        mData = data;
        mLogs = data.getLogList();
        mLimitCount = data.getLimitCount();
    }

    @Override
    public boolean needMoreLog() {
        return mLogs.size() < mLimitCount;
    }

    @Override
    public void addLog(Log log) {
        mLogs.add(log);
    }

    @Override
    public Report getNewReport(boolean isLoadToEnd) {
        Report report = new Report();
        report.logs.addAll(mLogs);
        report.expectCount = mLimitCount;
        report.actualCount = mLogs.size();
        report.endReason = getEndReason(isLoadToEnd);
        report.lastDataId = mData.getLastDataId();
        report.curDataId = mData.getCurDataId();
        report.nextDataId = mData.getNextDataId();

        SFileRange range = mData.getLoadRange();
        report.startPointer = range.start;
        report.endPointer = range.end;
        return report;
    }

    private String getEndReason(boolean isLoadToEnd) {
        if (!needMoreLog()) {
            return "已找到指定数量的结果";
        }
        if (isLoadToEnd) {
            return "已到达文件的末尾";
        }
        return "已到达限制区域的末尾";
    }
}
