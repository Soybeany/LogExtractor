package com.soybeany.logextractor.sfile.data;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IRenewalData {

    String getLastDataId();

    void setLastDataId(String id);

    String getCurDataId();

    void setCurDataId(String id);

    String getNextDataId();

    void setNextDataId(String id);

}
