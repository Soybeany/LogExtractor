package com.soybeany.log.base;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public interface IIndexCenter<RangeParam, Range, IndexParam, Index> {

    /**
     * 获得需加载的范围(查询用)
     */
    Range getLoadRange(RangeParam param);

    /**
     * 获得使用的索引(创建用)
     */
    Index getIndex(IndexParam param);

}
