package com.soybeany.logextractor.efb.handler;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.sfile.handler.ISFileIndexHandler;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.Collections;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class TimeIndexHandler implements ISFileIndexHandler<Param, Index, StdLine, StdFlag> {
    @Override
    public List<SFileRange> getRangeStrict(Param param, Index index) {
        SFileRange[] timeIndex = index.time.get(param.getDateNotNull());
        // 若没有建立索引则不作限制
        if (null == timeIndex) {
            return null;
        }
        // 尝试限制
        SFileRange from = null, to = null;
        if (null != param.fromTime) {
            int arrIndex = param.fromTime.toValue(false);
            from = timeIndex[arrIndex];
            if (null == from) {
                for (int i = arrIndex; i < timeIndex.length; i++) {
                    if (null != timeIndex[i]) {
                        from = timeIndex[i];
                        break;
                    }
                }
            }
            if (null == from) {
                throw new BusinessException("没有找到指定开始时间的记录");
            }
        }
        if (null != param.toTime) {
            int arrIndex = param.toTime.toValue(false);
            to = timeIndex[arrIndex];
            if (null == to) {
                for (int i = arrIndex; i > 0; i--) {
                    if (null != timeIndex[i]) {
                        to = timeIndex[i];
                        break;
                    }
                }
            }
            if (null == to) {
                throw new BusinessException("没有找到指定结束时间的记录");
            }
        }
        // 得到范围
        if (null != from && null != to) {
            return Collections.singletonList(SFileRange.between(from.start, to.end));
        }
        if (null != from) {
            return Collections.singletonList(SFileRange.from(from.start));
        }
        if (null != to) {
            return Collections.singletonList(SFileRange.to(to.end));
        }
        return null;
    }

    @Override
    public void onCreateIndexWithLine(Index index, StdLine stdLine, SFileRange lineRange) {
        String time = stdLine.info.time;
        String date = time.substring(0, 8);
        // 按日期索引
        SFileRange[] timeIndex = index.time.get(date);
        if (null == timeIndex) {
            index.time.put(date, timeIndex = new SFileRange[1440]);
        }
        // 按时间
        int arrIndex = Index.getTimeValue(time, false);
        // 在第一次出现此时间时，新增位点，并更新开始位点
        SFileRange range = timeIndex[arrIndex];
        if (null == range) {
            range = timeIndex[arrIndex] = SFileRange.from(lineRange.start);
        }
        // 更新结束位点
        range.updateEnd(lineRange.end);
    }

    @Override
    public void onCreateIndexWithFlag(Index index, StdFlag stdFlag, SFileRange flagRange) {
        // 留空
    }

}
