package com.soybeany.logextractor.sfile.merge;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/16.
 */
@SuppressWarnings("UnusedReturnValue")
public class RangeMerger {

    private static final int MAX_FLAG = 1 << 30;

    private final List<MNode> mNodes = new LinkedList<MNode>();
    private int mNextFlag = 1;

    /**
     * 获得交集
     */
    public static int getIntersection(int flagA, int flagB) {
        return flagA | flagB;
    }

    /**
     * 获得并集
     */
    public static int[] getUnion(int flagA, int flagB) {
        return new int[]{flagA, flagB, getIntersection(flagA, flagB)};
    }

    /**
     * @return 该批范围的标识
     */
    public synchronized int merge(List<SFileRange> ranges) {
        if (null == ranges) {
            return -1;
        }
        if (mNextFlag >= MAX_FLAG) {
            throw new BusinessException("范围flag已到达上限");
        }
        try {
            merge(mNextFlag, ranges);
            return mNextFlag;
        } finally {
            mNextFlag <<= 1;
        }
    }

    public void merge(int flag, List<SFileRange> ranges) {
        for (SFileRange range : ranges) {
            mNodes.add(new MNode(flag, range.start, true));
            mNodes.add(new MNode(flag, range.end, false));
        }
    }

    public MResult getResult() {
        return new MResult(mNodes);
    }

}
