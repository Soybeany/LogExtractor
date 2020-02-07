package com.soybeany.core.sort.parser;

import com.soybeany.core.common.Module;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseParser<Data, Source, Target> extends Module<Data> {

    public abstract Target parse(Source source);

}
