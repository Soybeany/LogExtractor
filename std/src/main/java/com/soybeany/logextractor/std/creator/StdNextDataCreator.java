package com.soybeany.logextractor.std.creator;

import com.google.gson.Gson;
import com.soybeany.logextractor.core.data.IDataIdAccessor;
import com.soybeany.logextractor.sfile.duplicator.SFileNextDataCreator;
import com.soybeany.logextractor.std.data.ILogStorageIdAccessor;

import java.util.UUID;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public class StdNextDataCreator<Data extends IDataIdAccessor & ILogStorageIdAccessor> extends SFileNextDataCreator<Data> {

    private static final Gson GSON = new Gson();

    public static String getNewDataId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public Data get(Data source) {
        Data data = (Data) GSON.fromJson(GSON.toJson(source), source.getClass());
        data.setCurDataId(getNewDataId());
        data.setLogStorageId(source.getLogStorageId());
        return data;
    }

}
