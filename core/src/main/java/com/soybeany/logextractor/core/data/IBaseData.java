package com.soybeany.logextractor.core.data;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IBaseData<Index, Report> extends IDataIdAccessor, IIndexAccessor<Index>, IReportAccessor<Report> {
}
