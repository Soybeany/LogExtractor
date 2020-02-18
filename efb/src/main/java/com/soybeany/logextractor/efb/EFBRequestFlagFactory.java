package com.soybeany.logextractor.efb;

import com.soybeany.logextractor.efb.data.flag.EFBRequestFlag;
import com.soybeany.logextractor.std.data.flag.StdFlag;
import com.soybeany.logextractor.std.data.flag.StdFlagInfo;
import com.soybeany.logextractor.std.parser.StdFlagParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public class EFBRequestFlagFactory extends StdFlagParser.FlagFactory {
    private static Pattern REQUEST_PATTERN = Pattern.compile("(.+?) (.+?\\.do)(?: \\{(.*)})?");

    private String mSender;

    public EFBRequestFlagFactory(String sender) {
        mSender = sender;
    }

    @Override
    public Pattern getPattern() {
        return REQUEST_PATTERN;
    }

    @Override
    public StdFlag get(Matcher matcher, StdFlagInfo info) {
        EFBRequestFlag flag = new EFBRequestFlag(info);
        flag.sender = mSender;
        flag.userNo = matcher.group(1);
        flag.url = matcher.group(2);
        flag.param = matcher.group(3);
        return flag;
    }
}
