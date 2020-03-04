package com.soybeany.logextractor.sfile.handler;

import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.ISFileParam;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/14.
 */
public abstract class SFileIndexHandlerFactory<Param extends ISFileParam, Index extends ISFileIndex, Line, Flag, Data, Log> extends BaseModule<Param, Data> {

    public static final int CALLBACK_SEQ = 70;

    @Override
    public int getCallbackSeq() {
        return CALLBACK_SEQ;
    }

    /**
     * 确定要建立什么索引，与查询用的Param无关，因为索引需要包含完整的信息，与是否查询没关系
     */
    public abstract List<? extends SFileIndexHandler<Param, Index, Line, Flag, Log>> getHandlerList();

}
