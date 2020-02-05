package com.soybeany.log.index;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public interface IIndexLoader<Position, Line> {

    void setOutset(Position pos);

    Line getNextLineInfo();

}
