package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.core.data.ICopiableIndex;
import com.soybeany.logextractor.sfile.data.ISFileIndex;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdIndex implements ISFileIndex {

    private final ReentrantLock mLock = new ReentrantLock();
    private long mPointer;

    @Override
    public Lock getLock() {
        return mLock;
    }

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
