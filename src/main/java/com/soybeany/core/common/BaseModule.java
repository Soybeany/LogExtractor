package com.soybeany.core.common;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseModule<Data> {
    public void onInit(Data data) {
        // 子类按需实现
    }

    public void onFinish() {
        // 子类按需实现
    }
}