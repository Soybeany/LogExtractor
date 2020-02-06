package com.soybeany.log.impl.loader.single;

import com.soybeany.log.base.IRawLine;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SFileRawLine implements IRawLine {

    private long mStartPointer;
    private long mEndPointer;

    private String mLine;

    @Override
    public String getLineText() {
        return mLine;
    }

    public long getStartPointer() {
        return mStartPointer;
    }

    public long getEndPointer() {
        return mEndPointer;
    }

    void update(long startPointer, long endPointer, String line) {
        mStartPointer = startPointer;
        mEndPointer = endPointer;
        mLine = line;
    }
}
