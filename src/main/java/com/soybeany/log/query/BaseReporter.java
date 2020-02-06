package com.soybeany.log.query;

import com.soybeany.log.base.Module;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseReporter<Data, Log, Report extends IReport> extends Module<Data> {

    public abstract boolean needMoreLog();

    public abstract void addLog(Log log);

    public abstract Report getReport();

}
