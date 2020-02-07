package com.soybeany.core.query;

import com.soybeany.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseFilterFactory<Data, Log> extends BaseModule<Data> {

    public abstract List<BaseFilter<Data, Log>> getFilters();

}
