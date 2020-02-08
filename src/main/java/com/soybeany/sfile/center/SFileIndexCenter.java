package com.soybeany.sfile.center;

import com.soybeany.core.common.ToolUtils;
import com.soybeany.core.impl.center.MemIndexCenter;
import com.soybeany.sfile.data.SFileRange;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/8.
 */
public class SFileIndexCenter<Data, Index> extends MemIndexCenter<Data, SFileRange, Index> {

    private RangeProvider<Data, Index> mRangeProvider;

    public SFileIndexCenter(IInfoProvider<Data, Index> infoProvider) {
        this(infoProvider, new RangeProvider<Data, Index>());
    }

    private SFileIndexCenter(IInfoProvider<Data, Index> infoProvider, RangeProvider<Data, Index> rangeProvider) {
        super(infoProvider, rangeProvider);
        mRangeProvider = rangeProvider;
    }

    public void addRangeProvider(IRangeProvider<Data, SFileRange, Index> provider) {
        ToolUtils.checkNull(provider, "SFileIndexCenter的provider不能为null");
        mRangeProvider.providers.add(provider);
    }

    // ****************************************内部类****************************************

    private static class RangeProvider<Data, Index> implements IRangeProvider<Data, SFileRange, Index> {
        private List<IRangeProvider<Data, SFileRange, Index>> providers = new LinkedList<IRangeProvider<Data, SFileRange, Index>>();

        @Override
        public SFileRange getLoadRange(String purpose, Index index, Data data) {
            SFileRange range = SFileRange.max();
            for (IRangeProvider<Data, SFileRange, Index> provider : providers) {
                SFileRange tmpRange = provider.getLoadRange(purpose, index, data);
                if (null == tmpRange) {
                    continue;
                }
                if (range.start < tmpRange.start) {
                    range.start = tmpRange.start;
                }
                if (range.end > tmpRange.end) {
                    range.end = tmpRange.end;
                }
            }
            return range;
        }
    }
}
