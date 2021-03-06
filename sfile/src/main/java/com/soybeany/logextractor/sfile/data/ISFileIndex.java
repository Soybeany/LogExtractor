package com.soybeany.logextractor.sfile.data;

import com.soybeany.logextractor.core.data.ICopiableIndex;
import com.soybeany.logextractor.core.data.ILockProvider;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public interface ISFileIndex extends ILockProvider, ICopiableIndex {

    /**
     * 获取已加载到的位置
     */
    long getPointer();

    /**
     * 设置已加载到的位置
     */
    void setPointer(long pointer);

}
