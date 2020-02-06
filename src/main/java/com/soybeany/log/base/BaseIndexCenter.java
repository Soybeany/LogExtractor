package com.soybeany.log.base;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCenter<Data, Range, Index> extends Module<Data> {

    /**
     * 获得需加载的范围(查询用)
     */
    public abstract Range getLoadRange();

    /**
     * 获得使用的索引(创建用)
     */
    public abstract Index getIndex();

}
