package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.IRenewalInfoAccessor;
import com.soybeany.logextractor.sfile.data.SFileData;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 子类必须重写{@link #beNextDataOf}方法，设置新增的字段
 * <br>Created by Soybeany on 2020/2/10.
 */
public abstract class StdData<Param, Index, Report> extends SFileData<Param, Index, Report> implements ILogStorageAccessor, IRenewalInfoAccessor, ILoadDataAccessor {

    private SFileRange mScanRange;
    private SFileRange mQueryRange;
    private Boolean mCanLoadMore;
    private Map<String, Log> mLogStorage = new LinkedHashMap<String, Log>();

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
    public Boolean canQueryMore() {
        return mCanLoadMore;
    }

    @Override
    public void setCanQueryMore(boolean flag) {
        mCanLoadMore = flag;
    }

    @Override
    public Map<String, Log> getLogStorage() {
        return mLogStorage;
    }

    @Override
    public void setLogStorage(Map<String, Log> storage) {
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
