package com.soybeany.logextractor.std.reporter;

import com.soybeany.logextractor.core.query.BaseLogReporter;
import com.soybeany.logextractor.sfile.data.IRenewalData;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.IStdFileLoaderData;
import com.soybeany.logextractor.std.data.IStdReporterParam;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.StdReport;

import java.util.LinkedList;
import java.util.List;

import static com.soybeany.logextractor.sfile.data.IRenewalData.*;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdLogReporter<Param extends IStdReporterParam, Data extends IStdFileLoaderData & IRenewalData> extends BaseLogReporter<Param, StdLog, StdReport, Data> {

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
        report.noNextDataReason = getNoNextDataReason();
        report.lastDataId = mData.getLastDataId();
        report.curDataId = mData.getCurDataId();
        report.nextDataId = mData.getNextDataId();
        report.queryLoad = mData.getQueryLoad();
        SFileRange scanRange = mData.getScanRange();
        if (null != scanRange) {
            report.totalScan = scanRange.end;
            report.newScan = scanRange.end - scanRange.start;
        }
        return report;
    }

    private String getNoNextDataReason() {
        String reason = mData.getNoNextDataReason();
        // 已有确定的原因
        if (null != reason) {
            if (REASON_EOR.equals(reason)) {
                return "已到达限制区域的末尾";
            }
            if (REASON_EOF.equals(reason)) {
                return "已到达文件的末尾";
            }
            if (REASON_NOT_LOAD.equals(reason)) {
                return "没有加载数据";
            }
            return "未指明的原因";
        }
        if (!needMoreLog()) {
            return "已找到指定数量的结果";
        }
        if (mData.isReachLoadLimit()) {
            return "已加载指定大小的文本";
        }
        return "未预料的原因";
    }

}
