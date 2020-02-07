package com.soybeany.core.query;

import com.soybeany.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseReporter<Data, Log, Report> extends BaseModule<Data> {

    public abstract boolean needMoreLog();

    public abstract void addLog(Log log);

    public abstract Report getNewReport(boolean isLoadToEnd);

}
