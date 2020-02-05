package com.soybeany.log.query;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface IFilter<Log> {

    boolean isFiltered(Log log);

}
