package com.soybeany.core.scan;

import com.soybeany.core.common.BaseModule;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCreator<Data, Index, RLine, Info> extends BaseModule<Data> {

    public abstract void onCreateIndex(Index index, RLine rLine, Info info);

}
