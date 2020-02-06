package com.soybeany.log.index;

import com.soybeany.log.base.Module;
import com.soybeany.log.base.IRawLine;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCreator<Data, Index, RLine extends IRawLine, Info> extends Module<Data> {

    public abstract void onCreateIndex(Index index, RLine rLine, Info info);

}
