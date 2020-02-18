package com.soybeany.logextractor.efb;

import com.soybeany.logextractor.efb.data.EFBData;
import com.soybeany.logextractor.efb.data.EFBParam;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.flag.StdFlagInfo;
import com.soybeany.logextractor.std.parser.StdFlagParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public class EFBFlagParser extends StdFlagParser<EFBParam, EFBData> {
    private static Pattern PATTERN = Pattern.compile("^FLAG-(.+?)-(.+?):(.*)");

    {
        addFactory("客户端", new EFBRequestFlagFactory("客户端"));
        addFactory("管理端", new EFBRequestFlagFactory("管理端"));
    }

    @Override
    protected StdFlagInfo toFlagInfo(StdLine line) {
        Matcher matcher = PATTERN.matcher(line.content);
        if (!matcher.find()) {
            return null;
        }
        StdFlagInfo flag = new StdFlagInfo(line.info);
        flag.type = matcher.group(1);
        flag.state = matcher.group(2);
        flag.detail = matcher.group(3);
        return flag;
    }
}
