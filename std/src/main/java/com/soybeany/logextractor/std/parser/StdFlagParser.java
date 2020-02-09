package com.soybeany.logextractor.std.parser;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.common.ToolUtils;
import com.soybeany.logextractor.core.query.parser.BaseFlagParser;
import com.soybeany.logextractor.std.data.Line;
import com.soybeany.logextractor.std.data.flag.Flag;
import com.soybeany.logextractor.std.data.flag.FlagInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public abstract class StdFlagParser<Data> extends BaseFlagParser<Line, Flag, Data> {

    private final Map<String, FlagFactory> mFactoryMap = new HashMap<String, FlagFactory>();

    public void addFactory(String type, FlagFactory factory) {
        ToolUtils.checkNull(factory, "FlagParser的FlagProvider不能为null");
        mFactoryMap.put(type, factory);
    }

    @Override
    public Flag parse(Line line) {
        FlagInfo info = toFlagInfo(line);
        if (null == info) {
            return null;
        }
        FlagFactory factory = mFactoryMap.get(info.type);
        if (null == factory) {
            throw new BusinessException("FlagDetail使用了不支持的类型");
        }
        Matcher matcher = factory.getPattern().matcher(info.detail);
        if (!matcher.find()) {
            throw new BusinessException("无法解析FlagDetail");
        }
        return factory.get(matcher, info);
    }

    protected abstract FlagInfo toFlagInfo(Line line);

    public static abstract class FlagFactory {

        public abstract Pattern getPattern();

        public abstract Flag get(Matcher matcher, FlagInfo info);
    }
}
