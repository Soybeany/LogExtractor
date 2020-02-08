package com.soybeany.core.common;

/**
 * 实现类需注意并发创建索引的问题
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCenter<Data, Range, Index> extends BaseModule<Data> {

    /**
     * 获得源索引
     */
    public abstract Index getSourceIndex();

    /**
     * 获得拷贝索引
     */
    public abstract Index getCopiedIndex();

    /**
     * 获得需加载的范围
     */
    public abstract Range getLoadRange(String purpose, Index index);
}
