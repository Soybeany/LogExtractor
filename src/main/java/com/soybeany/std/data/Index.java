package com.soybeany.std.data;

import com.soybeany.sfile.data.IIndex;
import com.soybeany.sfile.loader.SFileRange;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class Index implements IIndex<Index> {
    public final Map<String, long[]> time = new HashMap<String, long[]>();
    public final Map<String, SFileRange> thread = new HashMap<String, SFileRange>();
    public final Map<String, SFileRange> url = new HashMap<String, SFileRange>();

    @Override
    public Index copy() {
        Index index = new Index();
        index.time.putAll(time);
        index.thread.putAll(thread);
        index.url.putAll(url);
        return index;
    }
}
