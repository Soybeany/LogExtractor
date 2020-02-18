package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.common.BaseModule;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseIndexCreatorFactory<Param, Index, Line, Flag, Data> extends BaseModule<Param, Data> {

    public static final int CALLBACK_SEQ = 40;

    @Override
    public int getCallbackSeq() {
        return CALLBACK_SEQ;
    }

    public abstract List<? extends BaseIndexCreator<Index, Line>> getLineIndexCreators();

    public abstract List<? extends BaseIndexCreator<Index, Flag>> getFlagIndexCreators();
}
