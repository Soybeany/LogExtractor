package com.soybeany.log.core;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCenter<Data, Range, Index> extends Module<Data> {

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
