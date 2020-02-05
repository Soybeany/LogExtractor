package com.soybeany.log.query;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface ILogFactory<Line, Flag, Log> {

    Log addFlag(Flag flag);

    void addLine(Line line);

    void addContent(Line line, String content);

}
