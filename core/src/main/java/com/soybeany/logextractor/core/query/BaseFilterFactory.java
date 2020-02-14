package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseFilterFactory<Param, Log, Data> extends BaseModule<Param, Data> {

    public static final int PROCESS_NUM = 50;

    @Override
    public int getProcessNum() {
        return PROCESS_NUM;
    }

    public abstract List<? extends BaseFilter<Log>> getFilters();

}
