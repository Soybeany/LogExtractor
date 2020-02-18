package com.soybeany.logextractor.core.query.parser;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLineParser<Param, Line, Data> extends BaseParser<Param, String, Line, Data> {

    public static final int CALLBACK_SEQ = 20;

    @Override
    public int getCallbackSeq() {
        return CALLBACK_SEQ;
    }

    public abstract void addContent(Line line, String content);
}
