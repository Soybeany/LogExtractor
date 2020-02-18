package com.soybeany.logextractor.efb.data;

import com.soybeany.logextractor.core.data.ICopiableIndex;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.StdIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class EFBIndex extends StdIndex {
    public final Map<String, long[]> time = new HashMap<String, long[]>();
    public final Map<String, List<SFileRange>> url = new HashMap<String, List<SFileRange>>();
    public final Map<String, List<SFileRange>> user = new HashMap<String, List<SFileRange>>();

    @Override
    public void copy(ICopiableIndex index) {
        super.copy(index);
        if (!(index instanceof EFBIndex)) {
            return;
        }
        EFBIndex otherIndex = (EFBIndex) index;
        time.putAll(otherIndex.time);
        url.putAll(otherIndex.url);
        user.putAll(otherIndex.user);
    }
}
