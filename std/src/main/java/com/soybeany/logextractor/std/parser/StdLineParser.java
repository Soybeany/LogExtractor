package com.soybeany.logextractor.std.parser;

import com.soybeany.logextractor.core.query.parser.BaseLineParser;
import com.soybeany.logextractor.std.data.StdLine;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public abstract class StdLineParser<Param, Data> extends BaseLineParser<Param, StdLine, Data> {

    @Override
    public void addContent(StdLine line, String content) {
        line.content += "\n" + content;
    }

}
