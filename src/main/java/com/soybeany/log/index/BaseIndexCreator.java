package com.soybeany.log.index;

import com.soybeany.log.core.Module;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCreator<Data, Index, RLine, Info> extends Module<Data> {

    public abstract void onCreateIndex(Index index, RLine rLine, Info info);

}
