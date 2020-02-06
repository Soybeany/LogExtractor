package com.soybeany.log.query;

import com.soybeany.log.base.Module;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseFilterFactory<Data, Log> extends Module<Data> {

    public abstract List<BaseFilter<Data, Log>> getFilters();

}
