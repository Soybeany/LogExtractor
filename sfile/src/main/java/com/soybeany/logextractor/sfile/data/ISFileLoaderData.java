package com.soybeany.logextractor.sfile.data;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/13.
 */
public interface ISFileLoaderData {

    long getFileSize();

    void setFileSize(long size);

    /**
     * 断点加载的范围(断点加载时才会更新数据)
     */
    SFileRange getRenewalLoadRange();

    /**
     * 正在处理的行所在的范围
     */
    SFileRange getCurLineRange();

    /**
     * 入参，未处理的加载范围
     */
    List<SFileRange> getUnhandledLoadRanges();

    void setUnhandledLoadRanges(List<SFileRange> result);

    /**
     * 处理后的加载范围（在处理前为null）
     */
    List<SFileRange> getHandledLoadRanges();

    void setHandledLoadRanges(List<SFileRange> range);

    /**
     * 实际加载的最大范围（在处理前为最大范围）
     */
    SFileRange getActMaxLoadRange();
}
