package com.soybeany.logextractor.demo.handler;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.demo.data.Index;
import com.soybeany.logextractor.demo.data.Param;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.sfile.handler.ISFileIndexHandler;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/19.
 */
public abstract class BaseKeyIndexHandler implements ISFileIndexHandler<Param, Index, StdLine, StdFlag> {

    private static final long MAX_VALUE = Long.MAX_VALUE;
    private long mMaxCrossLength = 1000;

    protected void setupMaxCrossLength(long length) {
        mMaxCrossLength = length;
    }

    protected List<SFileRange> getRange(String desc, Map<String, LinkedList<SFileRange>> map, String key) {
        if (null == key) {
            return null;
        }
        List<SFileRange> result = new LinkedList<SFileRange>();
        for (Map.Entry<String, LinkedList<SFileRange>> entry : map.entrySet()) {
            if (entry.getKey().contains(key)) {
                result.addAll(entry.getValue());
            }
        }
        if (result.isEmpty()) {
            throw new BusinessException("没有找到指定“" + desc + "”的记录");
        }
        return result;
    }

    protected void addIndexValue(Map<String, LinkedList<SFileRange>> map, String key, StdFlag flag, SFileRange range) {
        // 确保列表已创建
        LinkedList<SFileRange> ranges = map.get(key);
        if (null == ranges) {
            map.put(key, ranges = new LinkedList<SFileRange>());
        }
        // 空列表则直接添加
        if (ranges.isEmpty()) {
            // 添加新范围
            if (StdFlag.STATE_START.equals(flag.state)) {
                ranges.add(SFileRange.between(range.start, MAX_VALUE));
            }
            // 添加到此处的范围
            else if (StdFlag.STATE_END.equals(flag.state)) {
                ranges.add(SFileRange.to(range.end));
            }
            return;
        }
        SFileRange last = ranges.getLast();
        // 开始标识
        if (StdFlag.STATE_START.equals(flag.state)) {
            // 允许范围内则更新范围
            if (range.start - last.end <= mMaxCrossLength) {
                last.updateEnd(MAX_VALUE);
            }
            // 超过最大允许范围则添加为新范围
            else {
                ranges.add(SFileRange.between(range.start, MAX_VALUE));
            }
        }
        // 结束标识
        else if (StdFlag.STATE_END.equals(flag.state)) {
            last.updateEnd(range.end);
        }
    }
}
