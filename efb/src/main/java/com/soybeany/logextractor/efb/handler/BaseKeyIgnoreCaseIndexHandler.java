package com.soybeany.logextractor.efb.handler;

import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/19.
 */
public abstract class BaseKeyIgnoreCaseIndexHandler extends BaseKeyIndexHandler {

    protected List<SFileRange> getRange(String desc, Map<String, LinkedList<SFileRange>> map, String key) {
        return super.getRange(desc, map, null != key ? key.toLowerCase() : null);
    }

    protected void addIndexValue(Map<String, LinkedList<SFileRange>> map, String key, StdFlag flag, SFileRange range) {
        super.addIndexValue(map, key.toLowerCase(), flag, range);
    }

}
