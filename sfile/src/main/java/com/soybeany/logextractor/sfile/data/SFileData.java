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
    private List<SFileRange> mExpectLoadRange;
    private List<SFileRange> mActLoadRange;

    private long mStartPointer;
    private long mCurEndPointer;
    private long mTargetEndPointer = Long.MAX_VALUE;

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
    public List<SFileRange> getExceptLoadRanges() {
        return mExpectLoadRange;
    }

    @Override
    public void setExceptLoadRanges(List<SFileRange> ranges) {
        mExpectLoadRange = ranges;
    }

    @Override
    public List<SFileRange> getActLoadRanges() {
        return mActLoadRange;
    }

    @Override
    public void setActLoadRanges(List<SFileRange> range) {
        mActLoadRange = range;
    }

    @Override
    public long getStartPointer() {
        return mStartPointer;
    }

    @Override
    public void setStartPointer(long pointer) {
        mStartPointer = pointer;
    }

    @Override
    public long getCurEndPointer() {
        return mCurEndPointer;
    }

    @Override
    public void setCurEndPointer(long pointer) {
        mCurEndPointer = pointer;
    }

    @Override
    public long getTargetEndPointer() {
        return mTargetEndPointer;
    }

    @Override
    public void setTargetEndPointer(long pointer) {
        mTargetEndPointer = pointer;
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
        setStartPointer(data.getCurEndPointer());
        // 设置期望范围
        setExceptLoadRanges(data.getExceptLoadRanges());
    }
}
