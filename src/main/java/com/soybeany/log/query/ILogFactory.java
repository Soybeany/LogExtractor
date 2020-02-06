package com.soybeany.log.query;

import com.soybeany.log.base.IParamRecipient;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface ILogFactory<Param, Line, Flag, Log> extends IParamRecipient<Param> {

    void addLine(Line line);

    Log addFlag(Flag flag);

}
