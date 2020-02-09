package com.soybeany.logextractor.std.parser;

import com.soybeany.logextractor.core.query.parser.BaseLineParser;
import com.soybeany.logextractor.sfile.data.SFileRawLine;
import com.soybeany.logextractor.std.data.Line;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public abstract class StdLineParser<Data> extends BaseLineParser<Data, SFileRawLine, Line> {

    @Override
    public String getLineText(SFileRawLine rLine) {
        return rLine.getLineText();
    }

    @Override
    public void addContent(Line line, String content) {
        line.content += "\n" + content;
    }

}
