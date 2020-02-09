package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseModule;

/**
 * 实现类需注意并发修改同一个Log的问题
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLogFactory<Line, Flag, Log, Data> extends BaseModule<Data> {

    public abstract void addLine(Line line);

    public abstract Log addFlag(Flag flag);

}
