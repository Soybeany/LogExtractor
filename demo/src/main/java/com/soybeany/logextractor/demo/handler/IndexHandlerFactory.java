package com.soybeany.logextractor.demo.handler;

import com.soybeany.logextractor.demo.data.Data;
import com.soybeany.logextractor.demo.data.Index;
import com.soybeany.logextractor.demo.data.Param;
import com.soybeany.logextractor.sfile.handler.SFileIndexHandler;
import com.soybeany.logextractor.sfile.handler.SFileIndexHandlerFactory;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.Arrays;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class IndexHandlerFactory extends SFileIndexHandlerFactory<Param, Index, StdLine, StdFlag, Data, StdLog> {
    @Override
    public List<? extends SFileIndexHandler<Param, Index, StdLine, StdFlag, StdLog>> getHandlerList() {
        return Arrays.asList(new TimeIndexHandler(), new UrlIndexHandler(), new UserNoIndexHandler());
    }
}
