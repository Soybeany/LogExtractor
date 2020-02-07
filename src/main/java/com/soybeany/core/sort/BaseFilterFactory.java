package com.soybeany.core.sort;

import com.soybeany.core.common.Module;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseFilterFactory<Data, Log> extends Module<Data> {

    public abstract List<BaseFilter<Data, Log>> getFilters();

}
