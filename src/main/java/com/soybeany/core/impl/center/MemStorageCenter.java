package com.soybeany.core.impl.center;

import com.soybeany.core.common.BaseStorageCenter;
import com.soybeany.core.common.DataIdException;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class MemStorageCenter<Data> extends BaseStorageCenter<Data> {

    private static final SimpleLruStorage DATA_STORAGE = new SimpleLruStorage();

    public static void setDataCapacity(int count) {
        DATA_STORAGE.setCapacity(count);
    }

    public static void clearDataStorage() {
        DATA_STORAGE.clear();
    }

    @Override
    public Data loadData(String dataId) throws DataIdException {
        return DATA_STORAGE.getAndCheck(dataId, true);
    }

    @Override
    public void saveData(String dataId, Data data) throws DataIdException {
        DATA_STORAGE.putAndCheck(dataId, data);
    }

}
