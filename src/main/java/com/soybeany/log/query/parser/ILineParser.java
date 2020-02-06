package com.soybeany.log.query.parser;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface ILineParser<Param, Line> extends IParser<Param, String, Line> {

    void addContent(Line line, String content);
}
