package com.soybeany.logextractor.core.center;

import com.soybeany.logextractor.core.common.BaseStorageCenter;
import com.soybeany.logextractor.core.common.ToolUtils;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class MemStorageCenter<Index, Data> extends BaseStorageCenter<Index, Data> {

    private static final SimpleLruStorage<String, Object> INDEX_STORAGE = new SimpleLruStorage<String, Object>();
    private static final SimpleLruStorage<String, Object> DATA_STORAGE = new SimpleLruStorage<String, Object>();

    private final IIndexProvider<Index, Data> mIndexProvider;

    public MemStorageCenter(IIndexProvider<Index, Data> infoProvider) {
        ToolUtils.checkNull(infoProvider, "StorageCenter的IndexProvider未设置");
        mIndexProvider = infoProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Data loadData(String dataId) {
        return (Data) DATA_STORAGE.getAndCheck(dataId, true);
    }

    @Override
    public void saveData(String dataId, Data data) {
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

    @SuppressWarnings("unchecked")
    private Index getIndex(Data data) {
        String indexKey = mIndexProvider.getIndexKey(data);
        Index index = (Index) INDEX_STORAGE.get(indexKey);
        if (null == index) {
            INDEX_STORAGE.put(indexKey, index = mIndexProvider.getNewIndex());
        }
        return index;
    }

    // ****************************************接口****************************************

    public interface IIndexProvider<Index, Data> {

        String getIndexKey(Data data);

        Index getNewIndex();

        Index getCopy(Index source);
    }
}
