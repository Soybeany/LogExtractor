package com.soybeany.sfile.center;

import com.soybeany.core.common.BaseStorageCenter;
import com.soybeany.core.common.DataIdException;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class MemStorageCenter<Data, Report> extends BaseStorageCenter<Data, Report> {

    private static final SimpleLruStorage DATA_STORAGE = new SimpleLruStorage();
    private static final SimpleLruStorage REPORT_STORAGE = new SimpleLruStorage();

    static {
        setReportCapacity(5);
    }

    public static void setDataCapacity(int count) {
        DATA_STORAGE.setCapacity(count);
    }

    public static void setReportCapacity(int count) {
        REPORT_STORAGE.setCapacity(count);
    }

    public static void clearDataStorage() {
        DATA_STORAGE.clear();
    }

    public static void clearReportStorage() {
        REPORT_STORAGE.clear();
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
    public Report loadReport(String dataId) throws DataIdException {
        return REPORT_STORAGE.getAndCheck(dataId, false);
    }

    @Override
    public void saveReport(String dataId, Report report) throws DataIdException {
        REPORT_STORAGE.putAndCheck(dataId, report);
    }

}
