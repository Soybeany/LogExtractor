package com.soybeany.logextractor.core.scan;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseIndexCreator<Index, Info> {

    public abstract void onCreateIndex(Index index, Info info);

}
