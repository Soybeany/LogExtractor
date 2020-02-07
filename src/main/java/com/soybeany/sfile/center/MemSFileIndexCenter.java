package com.soybeany.sfile.center;

import com.soybeany.sfile.data.IIndex;
import com.soybeany.sfile.data.ISFileData;
import com.soybeany.core.common.BaseIndexCenter;
import com.soybeany.core.common.ToolUtils;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class MemSFileIndexCenter<Data extends ISFileData, Range, Index extends IIndex<Index>> extends BaseIndexCenter<Data, Range, Index> {

    private static final SimpleLruStorage STORAGE = new SimpleLruStorage();

    private final ICallback<Data, Range, Index> mCallback;
    private Data mData;

    public MemSFileIndexCenter(ICallback<Data, Range, Index> callback) {
        ToolUtils.checkNull(callback, "IndexCenter的Callback未设置");
        mCallback = callback;
    }

    @Override
    public void onInit(Data data) {
        super.onInit(data);
        mData = data;
    }

    @Override
    public Range getLoadRange(String purpose, Index index) {
        return mCallback.getLoadRange(purpose, index, mData);
    }

    @Override
    public Index getSourceIndex() {
        String indexKey = mCallback.getIndexKey(mData);
        Index index = STORAGE.get(indexKey);
        if (null == index) {
            STORAGE.put(indexKey, index = mCallback.getNewIndex());
        }
        return index;
    }

    @Override
    public Index getCopiedIndex() {
        return getSourceIndex().copy();
    }

    // ****************************************接口****************************************

    public interface ICallback<Data extends ISFileData, Range, Index> {

        Range getLoadRange(String purpose, Index index, Data data);

        String getIndexKey(Data data);

        Index getNewIndex();
    }
}