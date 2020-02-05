package com.soybeany.log.index;

import com.soybeany.log.base.ISeniorLine;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public interface IIndexCreator<Index, SLine extends ISeniorLine, Info> {

    void onCreateIndex(Index index, SLine sLine, Info info);

}
