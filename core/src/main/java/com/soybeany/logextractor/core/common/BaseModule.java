package com.soybeany.logextractor.core.common;

/**
 * 若在onActivate缓存了Data，建议在onInactivate中置空
 * <br>Created by Soybeany on 2020/2/6.
 */
public abstract class BaseModule<Param, Data> implements Comparable<BaseModule<Param, Data>> {

    @Override
    public int compareTo(BaseModule<Param, Data> o) {
        return getCallbackSeq() - o.getCallbackSeq();
    }

    /**
     * 流程开始时进行的回调
     */
    public void onStart(Param param, Data data) throws Exception {
        // 子类按需实现
    }

    /**
     * 流程结束时进行的回调
     */
    public void onFinish() throws Exception {
        // 子类按需实现
    }

    /**
     * 回调执行时的顺序：onStart时小的会先被回调，onFinish时小的会晚被回调
     */
    public abstract int getCallbackSeq();
}
