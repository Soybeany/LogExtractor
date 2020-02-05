package com.soybeany.log.query;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface IReporter<Log> {

    boolean needMoreLog();

    void addLog(Log log);

    String toResult();

}
