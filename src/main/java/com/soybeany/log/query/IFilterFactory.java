package com.soybeany.log.query;

import com.soybeany.log.base.IParamRecipient;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public interface IFilterFactory<Param, Log> extends IParamRecipient<Param> {

    List<IFilter<Param, Log>> getFilters();

}
