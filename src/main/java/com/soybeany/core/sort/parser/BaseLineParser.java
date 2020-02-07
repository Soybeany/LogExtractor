package com.soybeany.core.sort.parser;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLineParser<Data, RLine, Line> extends BaseParser<Data, String, Line> {

    public abstract String getLineText(RLine rLine) throws IOException;

    public abstract void addContent(Line line, String content);
}
