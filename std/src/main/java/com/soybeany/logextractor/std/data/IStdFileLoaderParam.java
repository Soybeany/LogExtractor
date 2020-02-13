package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.ISFileLoaderParam;

/**
 * <br>Created by Soybeany on 2020/2/13.
 */
public interface IStdFileLoaderParam extends ISFileLoaderParam {

    long getLoadSizeLimit();

}
