package com.soybeany.log.query;

import com.soybeany.log.base.Module;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLogFactory<Data, Line, Flag, Log> extends Module<Data> {

    public abstract void addLine(Line line);

    public abstract Log addFlag(Flag flag);

}
