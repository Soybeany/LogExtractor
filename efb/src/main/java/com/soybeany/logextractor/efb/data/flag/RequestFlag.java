package com.soybeany.logextractor.efb.data.flag;

import com.soybeany.logextractor.std.data.flag.StdFlag;
import com.soybeany.logextractor.std.data.flag.StdRequestFlag;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class RequestFlag extends StdRequestFlag {

    public String userNo;

    public RequestFlag(StdFlag flag) {
        super(flag);
    }
}
