package com.soybeany.logextractor.sfile.data;

import java.util.List;

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

    List<SFileRange> getExceptLoadRanges();

    void setExceptLoadRanges(List<SFileRange> result);

    List<SFileRange> getActLoadRanges();

    void setActLoadRanges(List<SFileRange> range);
}
