package com.soybeany.logextractor.core.data;

import java.util.concurrent.locks.Lock;

/**
 * <br>Created by Soybeany on 2020/2/21.
 */
public interface ILockProvider {

    Lock getLock();

}
