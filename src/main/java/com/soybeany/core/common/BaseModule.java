package com.soybeany.core.common;

/**
 * 若在onActivate缓存了Data，建议在onInactivate中置空
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseModule<Data> {
    public void onActivate(Data data) {
        // 子类按需实现
    }

    public void onInactivate() {
        // 子类按需实现
    }
}
