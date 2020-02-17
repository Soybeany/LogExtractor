package com.soybeany.logextractor.sfile.merge;

/**
 * <br>Created by Soybeany on 2020/2/16.
 */
public class MNode implements Comparable<MNode> {

    public int flag;
    public long index;
    public boolean isStart;

    public MNode(int flag, long index, boolean isStart) {
        this.flag = flag;
        this.index = index;
        this.isStart = isStart;
    }

    @Override
    public int compareTo(MNode o) {
        return index > o.index ? 1 : index < o.index ? -1 : 0;
    }
}
