package com.soybeany.logextractor.sfile.data;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.data.BaseData;

import java.util.List;

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

    private String mLastId;
    private String mCurId;
    private String mNextId;
    private String mReason;

    private long mFileSize;
    private final SFileRange mCurLineRange = SFileRange.empty();
    private final SFileRange mNeedLoadRange = SFileRange.max();
    private List<SFileRange> mExpectLoadRanges;
    private List<SFileRange> mActLoadRanges;

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
    public SFileRange getNeedLoadRange() {
        return mNeedLoadRange;
    }

    @Override
    public SFileRange getCurLineRange() {
        return mCurLineRange;
    }

    @Override
    public List<SFileRange> getExceptLoadRanges() {
        return mExpectLoadRanges;
    }

    @Override
    public void setExceptLoadRanges(List<SFileRange> ranges) {
        mExpectLoadRanges = ranges;
    }

    @Override
    public List<SFileRange> getActLoadRanges() {
        return mActLoadRanges;
    }

    @Override
    public void setActLoadRanges(List<SFileRange> range) {
        mActLoadRanges = range;
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
        mNeedLoadRange.updateStart(data.getCurLineRange().end);
        // 设置期望范围
        setExceptLoadRanges(data.getExceptLoadRanges());
    }
}
