package com.soybeany.logextractor.sfile.data;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.data.BaseData;

/**
 * 子类必须重写{@link #beNextDataOf}方法，设置新增的字段
 * <br>Created by Soybeany on 2020/2/10.
 */
public abstract class SFileData<Index, Report> extends BaseData<Index, Report> implements IFileInfoProvider, IRenewalInfoAccessor {

    private String mLastId;
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

    public void beNextDataOf(SFileData<Index, Report> data) {
        // 设置指定数据的下一数据连接
        if (null != data.getNextDataId()) {
            throw new BusinessException("指定数据已存在下一数据连接");
        }
        data.setNextDataId(getDataId());
        mLastId = data.getDataId();
    }
}
