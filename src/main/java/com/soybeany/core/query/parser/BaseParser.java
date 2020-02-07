package com.soybeany.core.query.parser;

import com.soybeany.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseParser<Data, Source, Target> extends BaseModule<Data> {

    public abstract Target parse(Source source);

}
