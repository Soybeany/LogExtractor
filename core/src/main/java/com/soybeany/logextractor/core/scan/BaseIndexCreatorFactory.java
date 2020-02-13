package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseIndexCreatorFactory<Param, Index, RLine, Line, Flag, Data> extends BaseModule<Param, Data> {

    public abstract List<? extends BaseIndexCreator<Param, Index, RLine, Line, Data>> getLineIndexCreators();

    public abstract List<? extends BaseIndexCreator<Param, Index, RLine, Flag, Data>> getFlagIndexCreators();
}
