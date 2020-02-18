package com.soybeany.logextractor.sfile.handler;

import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.ISFileParam;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/14.
 */
public abstract class SFileIndexHandlerFactory<Param extends ISFileParam, Index extends ISFileIndex, Line, Flag, Data> extends BaseModule<Param, Data> {

    public static final int CALLBACK_SEQ = 70;

    @Override
    public int getCallbackSeq() {
        return CALLBACK_SEQ;
    }

    public abstract List<ISFileIndexHandler<Param, Index, Line, Flag>> getHandlerList();

}
