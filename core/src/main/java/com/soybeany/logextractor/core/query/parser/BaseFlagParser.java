package com.soybeany.logextractor.core.query.parser;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseFlagParser<Param, Line, Flag, Data> extends BaseParser<Param, Line, Flag, Data> {

    public static final int CALLBACK_SEQ = 30;

    @Override
    public int getCallbackSeq() {
        return CALLBACK_SEQ;
    }

}
