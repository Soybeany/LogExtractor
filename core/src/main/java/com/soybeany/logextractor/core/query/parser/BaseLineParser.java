package com.soybeany.logextractor.core.query.parser;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLineParser<Param, Line, Data> extends BaseParser<Param, String, Line, Data> {

    public abstract void addContent(Line line, String content);
}
