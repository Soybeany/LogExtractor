package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseQueryReporter<Log, Report, Data> extends BaseModule<Data> {

    public abstract boolean needMoreLog();

    public abstract void addLog(Log log);

    public abstract Report getNewReport();

}
