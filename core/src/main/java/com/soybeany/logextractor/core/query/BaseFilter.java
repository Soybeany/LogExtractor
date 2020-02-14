package com.soybeany.logextractor.core.query;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseFilter<Log> {

    public abstract boolean isFiltered(Log log);

}
