package com.soybeany.logextractor.sfile.merge;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.*;

/**
 * <br>Created by Soybeany on 2020/2/16.
 */
public class MResult {

    private final List<MRange> mRanges = new LinkedList<MRange>();
    private int mIntersectionFlag;

    public MResult(int intersectionFlag, List<MNode> nodes) {
        mIntersectionFlag = intersectionFlag;
        setupRanges(nodes);
    }

    public List<SFileRange> getIntersectionRanges() {
        return getRanges(mIntersectionFlag);
    }

    public List<SFileRange> getRanges(int... flags) {
        Set<Integer> flagSet = new HashSet<Integer>();
        for (int flag : flags) {
            flagSet.add(flag);
        }
        return getRanges(flagSet);
    }

    private void setupRanges(List<MNode> nodes) {
        Collections.sort(nodes);
        int lastFlag = 0;
        long lastIndex = 0;
        Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        for (MNode node : nodes) {
            // 上一标识不为0，则表示能生成范围
            if (0 != lastFlag) {
                mRanges.add(new MRange(lastFlag, lastIndex, node.index));
            }
            // 更新标识
            Integer flagCount = countMap.get(node.flag);
            if (node.isStart) {
                if (null == flagCount) {
                    countMap.put(node.flag, 1);
                    lastFlag |= node.flag;
                } else {
                    countMap.put(node.flag, ++flagCount);
                }
            } else {
                if (null == flagCount) {
                    throw new BusinessException("范围缺少开始标识");
                }
                countMap.put(node.flag, --flagCount);
                if (0 > flagCount) {
                    throw new BusinessException("过多的范围结束标识");
                }
                if (0 == flagCount) {
                    countMap.remove(node.flag);
                    lastFlag ^= node.flag;
                }
            }
            // 更新下标
            lastIndex = node.index;
        }
    }

    private List<SFileRange> getRanges(Set<Integer> flags) {
        List<SFileRange> result = new LinkedList<SFileRange>();
        for (MRange range : mRanges) {
            if (flags.contains(range.flag)) {
                result.add(SFileRange.between(range.start, range.end));
            }
        }
        compressRange(result);
        return result;
    }

    private void compressRange(List<SFileRange> ranges) {
        Iterator<SFileRange> iterator = ranges.iterator();
        SFileRange lastRange = null;
        while (iterator.hasNext()) {
            SFileRange range = iterator.next();
            // 能合并则合并
            if (null == lastRange || lastRange.end != range.start) {
                lastRange = range;
                continue;
            }
            lastRange.updateEnd(range.end);
            iterator.remove();
        }
    }

    private static class MRange {
        public int flag;
        public long start;
        public long end;

        MRange(int flag, long start, long end) {
            this.flag = flag;
            this.start = start;
            this.end = end;
        }
    }
}
