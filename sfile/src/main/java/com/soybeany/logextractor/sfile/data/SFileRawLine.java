package com.soybeany.logextractor.sfile.data;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SFileRawLine {

    private long mStartPointer;
    private long mEndPointer;

    private String mLine;

    public long getStartPointer() {
        return mStartPointer;
    }

    public long getEndPointer() {
        return mEndPointer;
    }

    public String getLineText() {
        return mLine;
    }

    public void update(long startPointer, long endPointer, String line) {
        mStartPointer = startPointer;
        mEndPointer = endPointer;
        mLine = line;
    }
}
