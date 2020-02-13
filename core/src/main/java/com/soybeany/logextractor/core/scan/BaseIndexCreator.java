package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCreator<Param, Index, Info, Data> extends BaseModule<Param, Data> {

    public abstract void onCreateIndex(Index index, Info info);

}
