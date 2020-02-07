package com.soybeany.log.sort;

import com.soybeany.log.core.Module;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseFilter<Data, Log> extends Module<Data> {

    public abstract boolean isFiltered(Log log);

}
