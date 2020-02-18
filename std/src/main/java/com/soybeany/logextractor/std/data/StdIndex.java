package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.core.data.ICopiableIndex;
import com.soybeany.logextractor.sfile.data.ISFileIndex;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdIndex implements ISFileIndex {

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
        if (!(index instanceof StdIndex)) {
            return;
        }
        StdIndex otherIndex = (StdIndex) index;
        mPointer = otherIndex.mPointer;
    }
}
