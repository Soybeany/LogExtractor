package com.soybeany.logextractor.efb;

import com.soybeany.logextractor.std.data.flag.Flag;
import com.soybeany.logextractor.std.data.flag.RequestFlag;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class EFBRequestFlag extends RequestFlag {

    public String userNo;

    public EFBRequestFlag(Flag flag) {
        super(flag);
    }
}
