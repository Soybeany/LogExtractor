package com.soybeany.sfile.data;

import com.soybeany.sfile.loader.SFileRange;

import java.io.File;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public interface ISFileData {

    File getFileToLoad();

    String getFileCharset();

    SFileRange getLoadRange();

    void setLoadRange(SFileRange range);
}
