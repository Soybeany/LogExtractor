package com.soybeany.logextractor.sfile.data;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.data.BaseData;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 子类必须重写{@link #beNextDataOf}方法，设置新增的字段
 * <br>Created by Soybeany on 2020/2/10.
 */
public abstract class SFileData<Param, Index, Report> extends BaseData<Index> implements ISFileLoaderData, IRenewalData {

    /**
     * 用于汇总查询结果的报告
     */
    public Report report;

    /**
     * 外部入参，用户侧
     */
    public Param param;

    private final ReentrantLock mLock = new ReentrantLock();

    private String mLastId;
    private String mCurId;
    private String mNextId;
    private String mReason;

    private long mFileSize;
    private final SFileRange mCurLineRange = SFileRange.empty();
    private final SFileRange mRenewalLoadRange = SFileRange.max();
    private List<SFileRange> mUnhandledLoadRanges;
    private List<SFileRange> mHandledLoadRanges;
    private SFileRange mActMaxLoadRange = SFileRange.max();

    @Override
    public ReentrantLock getLock() {
        return mLock;
    }

    @Override
    public String getLastDataId() {
        return mLastId;
    }

    @Override
    public void setLastDataId(String id) {
        mLastId = id;
    }

    @Override
    public String getCurDataId() {
        return mCurId;
    }

    @Override
    public void setCurDataId(String id) {
        mCurId = id;
    }

    @Override
    public String getNextDataId() {
        return mNextId;
    }

    @Override
    public void setNextDataId(String id) {
        mNextId = id;
    }

    @Override
    public String getNoNextDataReason() {
        return mReason;
    }

    @Override
    public void setNoNextDataReason(String reason) {
        mReason = reason;
    }

    @Override
    public long getFileSize() {
        return mFileSize;
    }

    @Override
    public void setFileSize(long size) {
        mFileSize = size;
    }

    @Override
    public SFileRange getRenewalLoadRange() {
        return mRenewalLoadRange;
    }

    @Override
    public SFileRange getCurLineRange() {
        return mCurLineRange;
    }

    @Override
    public List<SFileRange> getUnhandledLoadRanges() {
        return mUnhandledLoadRanges;
    }

    @Override
    public void setUnhandledLoadRanges(List<SFileRange> ranges) {
        mUnhandledLoadRanges = ranges;
    }

    @Override
    public List<SFileRange> getHandledLoadRanges() {
        return mHandledLoadRanges;
    }

    @Override
    public void setHandledLoadRanges(List<SFileRange> range) {
        mHandledLoadRanges = range;
    }

    @Override
    public SFileRange getActMaxLoadRange() {
        return mActMaxLoadRange;
    }

    public void beNextDataOf(SFileData<Param, Index, Report> data) {
        if (null != data.getNextDataId()) {
            throw new BusinessException("指定数据已存在下一数据连接");
        }
        // 设置id
        data.setNextDataId(getCurDataId());
        setLastDataId(data.getCurDataId());
        // 设置参数
        param = data.param;
        // 设置位点
        mRenewalLoadRange.updateStart(data.getCurLineRange().end);
        // 设置期望范围
        setUnhandledLoadRanges(data.getUnhandledLoadRanges());
    }
}
