package com.soybeany.logextractor.core.data;

/**
 * 用于模块间交流的数据
 * <br>Created by Soybeany on 2020/2/10.
 */
public abstract class BaseData<Index> implements ILockProvider {

    /**
     * 索引，加载模块使用
     */
    public Index index;

}
