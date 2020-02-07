package com.soybeany.core.index;

import com.soybeany.core.common.Module;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCreator<Data, Index, RLine, Info> extends Module<Data> {

    public abstract void onCreateIndex(Index index, RLine rLine, Info info);

}
