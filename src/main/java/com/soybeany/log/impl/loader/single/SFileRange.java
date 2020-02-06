package com.soybeany.log.impl.loader.single;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SFileRange {

    public long start;
    public long end;

    public static SFileRange from(long start) {
        return new SFileRange(start, Long.MAX_VALUE);
    }

    public static SFileRange to(long end) {
        return new SFileRange(0, end);
    }

    public static SFileRange between(long start, long end) {
        return new SFileRange(start, end);
    }

    private SFileRange(long start, long end) {
        this.start = start;
        this.end = end;
    }
}
