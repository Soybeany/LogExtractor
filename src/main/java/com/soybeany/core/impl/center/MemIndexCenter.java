package com.soybeany.core.impl.center;

import com.soybeany.core.common.BaseIndexCenter;
import com.soybeany.core.common.ToolUtils;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class MemIndexCenter<Data, Range, Index> extends BaseIndexCenter<Data, Range, Index> {

    private static final SimpleLruStorage STORAGE = new SimpleLruStorage();

    private final IInfoProvider<Data, Index> mInfoProvider;
    private final IRangeProvider<Data, Range, Index> mRangeProvider;
    private Data mData;
    private Index mIndex;

    public MemIndexCenter(IInfoProvider<Data, Index> infoProvider, IRangeProvider<Data, Range, Index> rangeProvider) {
        ToolUtils.checkNull(rangeProvider, "IndexCenter的InfoProvider未设置");
        ToolUtils.checkNull(rangeProvider, "IndexCenter的DataProvider未设置");
        mInfoProvider = infoProvider;
        mRangeProvider = rangeProvider;
    }

    @Override
    public void onActivate(Data data) {
        super.onActivate(data);
        mIndex = getIndex(mData = data);
    }

    @Override
    public Range getLoadRange(String purpose, Index index) {
        return mRangeProvider.getLoadRange(purpose, index, mData);
    }

    @Override
    public Index getSourceIndex() {
        return mIndex;
    }

    @Override
    public Index getCopiedIndex() {
        return mInfoProvider.getNewIndex(mIndex);
    }

    // ****************************************内部方法****************************************

    private Index getIndex(Data data) {
        String indexKey = mInfoProvider.getIndexKey(data);
        Index index = STORAGE.get(indexKey);
        if (null == index) {
            STORAGE.put(indexKey, index = mInfoProvider.getNewIndex());
        }
        return index;
    }

    // ****************************************接口****************************************

    public interface IInfoProvider<Data, Index> {

        String getIndexKey(Data data);

        Index getNewIndex();

        Index getNewIndex(Index source);
    }

    public interface IRangeProvider<Data, Range, Index> {

        Range getLoadRange(String purpose, Index index, Data data);
    }
}
