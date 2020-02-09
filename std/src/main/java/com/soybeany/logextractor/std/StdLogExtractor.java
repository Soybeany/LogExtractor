package com.soybeany.logextractor.std;

import com.soybeany.logextractor.core.common.BaseStorageCenter;
import com.soybeany.logextractor.core.common.ConcurrencyException;
import com.soybeany.logextractor.core.common.DataIdException;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.sfile.accessor.SFileDataAccessor;
import com.soybeany.logextractor.sfile.data.ISFileData;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.data.IStdData;
import com.soybeany.logextractor.std.data.Line;
import com.soybeany.logextractor.std.data.Log;
import com.soybeany.logextractor.std.data.QueryReport;
import com.soybeany.logextractor.std.data.flag.Flag;

import java.io.IOException;

/**
 * 标准日志提取器
 * <br>提供标准的报告内容
 * <br>Created by Soybeany on 2020/2/8.
 */
public class StdLogExtractor<Data extends ISFileData & IStdData, Index extends ISFileIndex, Report extends QueryReport> extends SFileLogExtractor<Data, Index, Line, Flag, Log, Report> {

    private BaseStorageCenter<Data, Index> mStorageCenter;
    private SingleFileLoader<Data, Index> mLoader;
    private SFileDataAccessor<Data, Index, Report> mDataAccessor;

    // ****************************************设置方法****************************************

    @Override
    public void setStorageCenter(BaseStorageCenter<Data, Index> center) {
        super.setStorageCenter(center);
        mStorageCenter = center;
    }

    @Override
    public void setLoader(SingleFileLoader<Data, Index> loader) {
        super.setLoader(loader);
        mLoader = loader;
    }

    @Override
    public void setDataAccessor(SFileDataAccessor<Data, Index, Report> dataAccessor) {
        super.setDataAccessor(dataAccessor);
        mDataAccessor = dataAccessor;
    }

    // ****************************************输出API****************************************

    @Override
    public Report find(Data data) throws IOException, DataIdException, ConcurrencyException {
        Report report = super.find(data);
        onSetupReport(report, data);
        return report;
    }

    @Override
    public Report findById(String dataId) throws IOException, DataIdException, ConcurrencyException {
        Report report = super.findById(dataId);
        onSetupReport(report, mStorageCenter.loadData(dataId));
        return report;
    }

    // ****************************************子类方法****************************************

    @Override
    protected void onCreateIndexesFinish(Data data) {
        super.onCreateIndexesFinish(data);
//        mStorageCenter.getSourceIndex().getPointer();
    }

    protected void onSetupReport(Report report, Data data) {
        report.endReason = getEndReason(report.expectCount == report.actualCount, mLoader.isLoadToEnd());
        report.lastDataId = mDataAccessor.getLastDataId(data);
        report.curDataId = mDataAccessor.getCurDataId(data);
        report.nextDataId = mDataAccessor.getNextDataId(data);
    }

    // ****************************************内部方法****************************************

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
