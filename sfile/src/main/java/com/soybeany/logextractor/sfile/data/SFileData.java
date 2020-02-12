package com.soybeany.logextractor.sfile.data;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.data.BaseData;

/**
 * 子类必须重写{@link #beNextDataOf}方法，设置新增的字段
 * <br>Created by Soybeany on 2020/2/10.
 */
public abstract class SFileData<Param, Index, Report> extends BaseData<Index> implements IRenewalInfoAccessor {

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
    private long mPointer;

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
    public long getPointer() {
        return mPointer;
    }

    @Override
    public void setPointer(long pointer) {
        mPointer = pointer;
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
    }
}
