package com.soybeany.log.base;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public interface IIndexCenter<Param, Range, Index> extends IParamRecipient<Param> {

    /**
     * 获得需加载的范围(查询用)
     */
    Range getLoadRange();

    /**
     * 获得使用的索引(创建用)
     */
    Index getIndex();

}
