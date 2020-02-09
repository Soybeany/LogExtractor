package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCreator<Index, RLine, Info, Data> extends BaseModule<Data> {

    public abstract void onCreateIndex(Index index, RLine rLine, Info info);

}
