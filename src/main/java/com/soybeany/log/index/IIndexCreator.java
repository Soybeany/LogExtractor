package com.soybeany.log.index;

import com.soybeany.log.base.IRawLine;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public interface IIndexCreator<Index, RLine extends IRawLine, Info> {

    void onCreateIndex(Index index, RLine rLine, Info info);

}
