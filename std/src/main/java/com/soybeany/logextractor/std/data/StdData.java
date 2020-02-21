package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.SFileData;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 子类必须重写{@link #beNextDataOf}方法，设置新增的字段
 * <br>Created by Soybeany on 2020/2/10.
 */
public abstract class StdData<Param, Index, Report> extends SFileData<Param, Index, Report> implements IStdLogAssemblerData, IStdFileLoaderData, IStdTimingData {

    private SFileRange mScanRange;
    private Long mQueryLoad;
    private boolean mIsReachLoadLimit;
    private Map<String, StdLog> mLogStorage = new LinkedHashMap<String, StdLog>();
    private Long mScanSpend;
    private Long mQuerySpend;

    @Override
    public SFileRange getScanRange() {
        return mScanRange;
    }

    @Override
    public void setScanRange(SFileRange range) {
        mScanRange = range;
    }

    @Override
    public Long getQueryLoad() {
        return mQueryLoad;
    }

    @Override
    public void setQueryLoad(Long length) {
        mQueryLoad = length;
    }

    @Override
    public boolean isReachLoadLimit() {
        return mIsReachLoadLimit;
    }

    @Override
    public void setReachLoadLimit(boolean flag) {
        mIsReachLoadLimit = flag;
    }

    @Override
    public Map<String, StdLog> getLogStorage() {
        return mLogStorage;
    }

    @Override
    public Long getScanSpend() {
        return mScanSpend;
    }

    @Override
    public void setScanSpend(Long spend) {
        mScanSpend = spend;
    }

    @Override
    public Long getQuerySpend() {
        return mQuerySpend;
    }

    @Override
    public void setQuerySpend(Long spend) {
        mQuerySpend = spend;
    }

    @Override
    public void beNextDataOf(SFileData<Param, Index, Report> data) {
        super.beNextDataOf(data);
        if (!(data instanceof StdData)) {
            return;
        }
        StdData<Param, Index, Report> stdData = (StdData<Param, Index, Report>) data;
        mLogStorage = stdData.getLogStorage();
    }
}
