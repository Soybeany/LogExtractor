package com.soybeany.logextractor.sfile.data;

/**
 * <br>Created by Soybeany on 2020/2/13.
 */
public interface ISFileLoaderData {

    long getStartPointer();

    void setStartPointer(long pointer);

    long getCurEndPointer();

    void setCurEndPointer(long pointer);

    long getTargetEndPointer();

    void setTargetEndPointer(long pointer);

    long getFileSize();

    void setFileSize(long size);
}
