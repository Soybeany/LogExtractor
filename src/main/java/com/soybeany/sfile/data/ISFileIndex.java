package com.soybeany.sfile.data;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public interface ISFileIndex {

    /**
     * 获取已加载到的位置
     */
    long getPointer();

    /**
     * 设置已加载到的位置
     */
    void setPointer(long pointer);

}
