package com.fenghuo.observer;

/**
 * Created by 张少林
 * on 15-3-16 下午9:10
 * Email:zsl@kuba.bz
 * Android Studio
 * Function:
 */
public interface ClassifiedObserver<C, T> extends Observer<T> {
    void register(T observer, C... classifies);
}
