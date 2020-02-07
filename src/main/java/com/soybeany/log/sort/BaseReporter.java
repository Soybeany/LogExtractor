package com.soybeany.log.sort;

import com.soybeany.log.core.Module;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseReporter<Data, Log, Report> extends Module<Data> {

    public abstract boolean needMoreLog();

    public abstract void addLog(Log log);

    public abstract Report getReport();

}
