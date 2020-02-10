package com.soybeany.logextractor.core.data;

import com.soybeany.logextractor.core.common.BusinessException;

import java.util.UUID;

/**
 * <br>Created by Soybeany on 2020/2/10.
 */
public class BaseData<Index, Report> implements IDataIdAccessor, IIndexAccessor<Index>, IReportAccessor<Report> {

    private static IIdGenerator ID_GENERATOR = new UUIDGenerator();

    // ****************************************成员变量****************************************

    private final String mId = ID_GENERATOR.getNewId();

    private Index mIndex;

    private Report mReport;

    // ****************************************方法****************************************

    public static void setIdGenerator(IIdGenerator generator) {
        if (null == generator) {
            throw new BusinessException("IdGenerator不能为null");
        }
        ID_GENERATOR = generator;
    }

    @Override
    public String getDataId() {
        return mId;
    }

    @Override
    public Index getIndex() {
        return mIndex;
    }

    @Override
    public void setIndex(Index index) {
        mIndex = index;
    }

    @Override
    public Report getReport() {
        return mReport;
    }

    @Override
    public void setReport(Report report) {
        mReport = report;
    }

    // ****************************************接口****************************************

    public interface IIdGenerator {
        String getNewId();
    }

    public static class UUIDGenerator implements IIdGenerator {
        @Override
        public String getNewId() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    public static class SimpleIdGenerator implements IIdGenerator {
        private int a;

        @Override
        public synchronized String getNewId() {
            return ++a + "";
        }
    }
}
