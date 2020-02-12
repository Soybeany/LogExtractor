package com.soybeany.logextractor.core.query.parser;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLineParser<Param, RLine, Line, Data> extends BaseParser<Param, String, Line, Data> {

    public abstract String getLineText(RLine rLine) throws IOException;

    public abstract void addContent(Line line, String content);
}