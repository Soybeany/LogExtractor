package com.soybeany.logextractor.core.query.parser;

import com.soybeany.logextractor.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseParser<Data, Source, Target> extends BaseModule<Data> {

    public abstract Target parse(Source source);

}
