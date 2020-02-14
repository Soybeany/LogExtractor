package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.SFileData;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 子类必须重写{@link #beNextDataOf}方法，设置新增的字段
 * <br>Created by Soybeany on 2020/2/10.
 */
public abstract class StdData<Param, Index, Report> extends SFileData<Param, Index, Report> implements IStdLogAssemblerData, IStdFileLoaderData {

    private SFileRange mScanRange;
    private SFileRange mQueryRange;
    private boolean mIsReachLoadLimit;
    private Map<String, StdLog> mLogStorage = new LinkedHashMap<String, StdLog>();

    @Override
    public SFileRange getScanRange() {
        return mScanRange;
    }

    @Override
    public void setScanRange(SFileRange range) {
        mScanRange = range;
    }

    @Override
    public SFileRange getQueryRange() {
        return mQueryRange;
    }

    @Override
    public void setQueryRange(SFileRange range) {
        mQueryRange = range;
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
    public void setLogStorage(Map<String, StdLog> storage) {
        mLogStorage = storage;
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
