package com.fenghuo.observer;

/**
 * Created with Android Studio
 * <p/>
 * Project: lockscreen
 * Author: zhangshaolin(www.iooly.com)
 * Date:   14-7-14
 * Time:   上午10:58
 * Email:  app@iooly.com
 */
public interface Observer<T> {

    void register(T observer);

    void unregister(T observer);
}
