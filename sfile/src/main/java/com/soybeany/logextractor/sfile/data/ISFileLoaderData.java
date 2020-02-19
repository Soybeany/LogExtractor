package com.soybeany.logextractor.sfile.data;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/13.
 */
public interface ISFileLoaderData {

    long getFileSize();

    void setFileSize(long size);

    /**
     * 处理后得到的需要加载的最大范围
     */
    SFileRange getNeedLoadRange();

    /**
     * 正在处理的行所在的范围
     */
    SFileRange getCurLineRange();

    /**
     * 入参，期望加载的范围，未处理
     */
    List<SFileRange> getExceptLoadRanges();

    void setExceptLoadRanges(List<SFileRange> result);

    /**
     * 实际加载的范围，在加载完成前为null
     */
    List<SFileRange> getActLoadRanges();

    void setActLoadRanges(List<SFileRange> range);
}
