package com.soybeany.logextractor.efb;

import com.soybeany.logextractor.efb.data.EFBData;
import com.soybeany.logextractor.efb.data.EFBParam;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.parser.StdLineParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class EFBLineParser extends StdLineParser<EFBParam, EFBData> {
    private static Pattern PATTERN = Pattern.compile("\\[(.{17})] \\[(INFO|WARN|ERROR)] \\[(.*?)] \\{(.*?)}-(.*)");

    @Override
    public StdLine parse(String s) {
        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.find()) {
            return null;
        }
        StdLine line = new StdLine();
        line.info.time = matcher.group(1);
        line.info.level = matcher.group(2);
        line.info.thread = matcher.group(3);
        line.info.position = matcher.group(4);
        line.content = matcher.group(5);
        return line;
    }
}
