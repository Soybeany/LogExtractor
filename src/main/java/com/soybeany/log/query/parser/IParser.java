package com.soybeany.log.query.parser;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface IParser<Source, Target> {

    Target parse(Source source);

}
