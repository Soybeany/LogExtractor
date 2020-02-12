package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.data.ICopiableIndex;
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

    @Override
    public long getPointer() {
        return mPointer;
    }

    @Override
    public void setPointer(long pointer) {
        mPointer = pointer;
    }

    @Override
    public void copy(ICopiableIndex index) {
        if (!(index instanceof Index)) {
            throw new BusinessException("类型不匹配，无法拷贝");
        }
        Index otherIndex = (Index) index;
        time.putAll(otherIndex.time);
        thread.putAll(otherIndex.thread);
        url.putAll(otherIndex.url);
        mPointer = otherIndex.mPointer;
    }
}
