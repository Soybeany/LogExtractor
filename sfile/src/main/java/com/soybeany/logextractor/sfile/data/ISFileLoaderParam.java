package com.soybeany.logextractor.sfile.data;

import java.io.File;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public interface ISFileLoaderParam {

    File getFileToLoad();

    String getFileCharset();

}
