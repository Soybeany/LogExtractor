package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseCreatorFactory<Data, Index, RLine, Line, Flag> extends BaseModule<Data> {

    public abstract List<? extends BaseIndexCreator<Data, Index, RLine, Line>> getLineCreators();

    public abstract List<? extends BaseIndexCreator<Data, Index, RLine, Flag>> getFlagCreators();
}
