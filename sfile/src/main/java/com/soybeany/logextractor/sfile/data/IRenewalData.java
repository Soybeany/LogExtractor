package com.soybeany.logextractor.sfile.data;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IRenewalData {

    String REASON_EOF = "file";
    String REASON_EOR = "range";
    String REASON_NOT_LOAD = "notLoad";

    String getLastDataId();

    void setLastDataId(String id);

    String getCurDataId();

    void setCurDataId(String id);

    String getNextDataId();

    void setNextDataId(String id);

    String getNoNextDataReason();

    void setNoNextDataReason(String reason);
}
