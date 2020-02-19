package com.soybeany.logextractor.efb.data;

import com.soybeany.logextractor.core.data.ICopiableIndex;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.StdIndex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class Index extends StdIndex {
    public final Map<String, long[]> time = new HashMap<String, long[]>();
    public final Map<String, LinkedList<SFileRange>> url = new HashMap<String, LinkedList<SFileRange>>();
    public final Map<String, LinkedList<SFileRange>> userNo = new HashMap<String, LinkedList<SFileRange>>();

    @Override
    public void copy(ICopiableIndex index) {
        super.copy(index);
        if (!(index instanceof Index)) {
            return;
        }
        Index otherIndex = (Index) index;
        time.putAll(otherIndex.time);
        url.putAll(otherIndex.url);
        userNo.putAll(otherIndex.userNo);
    }
}
