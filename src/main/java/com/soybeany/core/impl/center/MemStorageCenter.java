package com.soybeany.core.impl.center;

import com.soybeany.core.common.BaseStorageCenter;
import com.soybeany.core.common.DataIdException;
import com.soybeany.core.common.ToolUtils;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class MemStorageCenter<Data, Index> extends BaseStorageCenter<Data, Index> {

    private static final SimpleLruStorage INDEX_STORAGE = new SimpleLruStorage();
    private static final SimpleLruStorage DATA_STORAGE = new SimpleLruStorage();

    private final IIndexProvider<Data, Index> mIndexProvider;

    public MemStorageCenter(IIndexProvider<Data, Index> infoProvider) {
        ToolUtils.checkNull(infoProvider, "StorageCenter的IndexProvider未设置");
        mIndexProvider = infoProvider;
    }

    @Override
    public Data loadData(String dataId) throws DataIdException {
        return DATA_STORAGE.getAndCheck(dataId, true);
    }

    @Override
    public void saveData(String dataId, Data data) throws DataIdException {
        DATA_STORAGE.putAndCheck(dataId, data);
    }

    @Override
    public Index getSourceIndex(Data data) {
        return getIndex(data);
    }

    @Override
    public Index getCopiedIndex(Data data) {
        return mIndexProvider.getCopy(getIndex(data));
    }

    public static void setDataCapacity(int count) {
        DATA_STORAGE.setCapacity(count);
    }

    public static void clearDataStorage() {
        DATA_STORAGE.clear();
    }

    // ****************************************内部方法****************************************

    private Index getIndex(Data data) {
        String indexKey = mIndexProvider.getIndexKey(data);
        Index index = INDEX_STORAGE.get(indexKey);
        if (null == index) {
            INDEX_STORAGE.put(indexKey, index = mIndexProvider.getNewIndex());
        }
        return index;
    }

    // ****************************************接口****************************************

    public interface IIndexProvider<Data, Index> {

        String getIndexKey(Data data);

        Index getNewIndex();

        Index getCopy(Index source);
    }
}
