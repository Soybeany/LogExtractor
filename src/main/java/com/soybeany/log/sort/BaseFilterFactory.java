package com.soybeany.log.sort;

import com.soybeany.log.core.Module;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseFilterFactory<Data, Log> extends Module<Data> {

    public abstract List<BaseFilter<Data, Log>> getFilters();

}
