package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseFilterFactory<Param, Log, Data> extends BaseModule<Param, Data> {

    public abstract List<? extends BaseFilter<Param, Log, Data>> getFilters();

}
