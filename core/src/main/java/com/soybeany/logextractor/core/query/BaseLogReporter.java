package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLogReporter<Param, Log, Report, Data> extends BaseModule<Param, Data> {

    public static final int CALLBACK_SEQ = 60;

    @Override
    public int getCallbackSeq() {
        return CALLBACK_SEQ;
    }

    public abstract boolean needMoreLog();

    public abstract void addLog(Log log);

    public abstract Report getNewReport();

}
