package com.soybeany.std;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class StdFlagWriter {

    private static IDelegate DELEGATE;

    public static void setDelegate(IDelegate delegate) {
        DELEGATE = delegate;
    }

    public static void write(String flagInfo, String detail) {
        DELEGATE.onWrite(flagInfo, detail);
    }

    public interface IDelegate {
        void onWrite(String flagInfo, String detail);
    }

}
