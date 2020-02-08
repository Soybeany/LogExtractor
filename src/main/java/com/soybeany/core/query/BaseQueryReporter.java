package com.soybeany.core.query;

import com.soybeany.core.common.BaseModule;
import com.soybeany.core.common.BaseStorageCenter;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseQueryReporter<Data, Log, Report> extends BaseModule<Data> {

    public abstract boolean needMoreLog();

    public abstract void addLog(Log log);

    public abstract Report getNewReport();

}
