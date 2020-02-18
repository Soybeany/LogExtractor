package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseLogFilterFactory<Param, Log, Data> extends BaseModule<Param, Data> {

    public static final int CALLBACK_SEQ = 50;

    @Override
    public int getCallbackSeq() {
        return CALLBACK_SEQ;
    }

    public abstract List<? extends BaseLogFilter<Log>> getLogFilters();

    public abstract List<? extends BaseLogFilter<Log>> getIncompleteLogFilters();

}
