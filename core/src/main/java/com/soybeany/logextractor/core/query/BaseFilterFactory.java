package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseFilterFactory<Log, Data> extends BaseModule<Data> {

    public abstract List<BaseFilter<Log, Data>> getFilters();

}
