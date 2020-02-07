package com.soybeany.core.query;

import com.soybeany.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseFilter<Data, Log> extends BaseModule<Data> {

    public abstract boolean isFiltered(Log log);

}
