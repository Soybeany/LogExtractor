package com.soybeany.log.query.parser;

import com.soybeany.log.base.IParamRecipient;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface IParser<Param, Source, Target> extends IParamRecipient<Param> {

    Target parse(Source source);

}
