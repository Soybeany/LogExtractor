package com.soybeany.logextractor.sfile.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/3/4.
 */
public interface ISFileLogFilterFactory<Log> {

    void setExtractFilters(List<BaseLogFilter<Log>> filters);

}
