package com.soybeany.log.query.parser;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLineParser<Data, Line> extends BaseParser<Data, String, Line> {

    public abstract void addContent(Line line, String content);
}
