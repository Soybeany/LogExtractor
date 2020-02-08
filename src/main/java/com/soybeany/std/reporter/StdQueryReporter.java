package com.soybeany.std.reporter;

import com.soybeany.core.query.BaseQueryReporter;
import com.soybeany.std.data.IStdData;
import com.soybeany.std.data.Log;
import com.soybeany.std.data.QueryReport;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdQueryReporter<Data extends IStdData> extends BaseQueryReporter<Data, Log, QueryReport> {

    private int mLogLimit;
    private List<Log> mLogs;

    @Override
    public void onActivate(Data data) {
        super.onActivate(data);
        mLogs = data.getLogList();
        mLogLimit = data.getLogLimit();
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
        return report;
    }
}
