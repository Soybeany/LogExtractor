package com.soybeany.logextractor.efb;

import com.soybeany.logextractor.std.data.flag.StdFlag;
import com.soybeany.logextractor.std.data.flag.StdRequestFlag;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class EFBRequestFlag extends StdRequestFlag {

    public String userNo;

    public EFBRequestFlag(StdFlag flag) {
        super(flag);
    }
}
