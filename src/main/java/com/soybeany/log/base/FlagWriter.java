package com.soybeany.log.base;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class FlagWriter {

    private static IDelegate DELEGATE;

    public static void setDelegate(IDelegate delegate) {
        DELEGATE = delegate;
    }

    public static void write(String flag, String content) {
        DELEGATE.onWrite(flag, content);
    }

    public interface IDelegate {
        void onWrite(String flag, String content);
    }

}
