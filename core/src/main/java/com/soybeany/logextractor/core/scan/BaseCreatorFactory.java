package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseCreatorFactory<Param, Index, RLine, Line, Flag, Data> extends BaseModule<Param, Data> {

    public abstract List<? extends BaseIndexCreator<Param, Index, RLine, Line, Data>> getLineCreators();

    public abstract List<? extends BaseIndexCreator<Param, Index, RLine, Flag, Data>> getFlagCreators();
}
