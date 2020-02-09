package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class Index implements ISFileIndex {
    public final Map<String, long[]> time = new HashMap<String, long[]>();
    public final Map<String, SFileRange> thread = new HashMap<String, SFileRange>();
    public final Map<String, SFileRange> url = new HashMap<String, SFileRange>();

    private long mPointer;

    public Index copy(Index index) {
        index.time.putAll(time);
        index.thread.putAll(thread);
        index.url.putAll(url);
        index.mPointer = mPointer;
        return index;
    }

    @Override
    public long getPointer() {
        return mPointer;
    }

    @Override
    public void setPointer(long pointer) {
        mPointer = pointer;
    }
}
