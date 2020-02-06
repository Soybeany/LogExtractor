package com.soybeany.log.query;

import com.soybeany.log.base.IParamRecipient;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface IReporter<Param, Log, Report extends IReport> extends IParamRecipient<Param> {

    boolean needMoreLog();

    void addLog(Log log);

    Report getReport();

}
