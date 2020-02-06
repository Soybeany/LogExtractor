package com.soybeany.log.query;

import com.soybeany.log.base.IParamRecipient;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface IFilter<Param, Log> extends IParamRecipient<Param> {

    boolean isFiltered(Log log);

}
