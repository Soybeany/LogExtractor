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
    public final Map<String, SFileRange[]> time = new HashMap<String, SFileRange[]>();
    public final Map<String, LinkedList<SFileRange>> url = new HashMap<String, LinkedList<SFileRange>>();
    public final Map<String, LinkedList<SFileRange>> userNo = new HashMap<String, LinkedList<SFileRange>>();

    public static int getTimeValue(String time, boolean needSec) {
        int hour = Integer.parseInt(time.substring(9, 11));
        int min = Integer.parseInt(time.substring(12, 14));
        if (!needSec) {
            return Param.Time.toValue(hour, min);
        }
        int sec = Integer.parseInt(time.substring(15, 17));
        return Param.Time.toValue(hour, min, sec);
    }

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
