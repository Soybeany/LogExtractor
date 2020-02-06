package com.soybeany.log.index;

import com.soybeany.log.base.Module;
import com.soybeany.log.base.IRawLine;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseCreatorFactory<Data, Index, RLine extends IRawLine, Line, Flag> extends Module<Data> {

    public abstract List<? extends BaseIndexCreator<Data, Index, RLine, Line>> getLineCreators();

    public abstract List<? extends BaseIndexCreator<Data, Index, RLine, Flag>> getFlagCreators();
}
