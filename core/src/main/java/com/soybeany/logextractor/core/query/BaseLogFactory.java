package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.common.ConcurrencyException;

/**
 * 实现类需注意并发修改同一个Log的问题
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLogFactory<Data, Line, Flag, Log> extends BaseModule<Data> {

    public abstract void attainLock() throws ConcurrencyException;

    public abstract void releaseLock();

    public abstract void addLine(Line line);

    public abstract Log addFlag(Flag flag);

}
