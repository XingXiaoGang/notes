package com.fenghuo.observer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with Android Studio
 * <p/>
 * Project: lockscreen
 * Author: zhangshaolin(www.iooly.com)
 * Date:   14-6-26
 * Time:   上午8:17
 * Email:  app@iooly.com
 */
public class MultiObserver<T> implements Observer<T> {

    private final List<WeakReference<T>> mListenerRefs =
            Collections.synchronizedList(new ArrayList<WeakReference<T>>());

    public final void register(T observer) {
        if (observer == null) {
            return;
        }
        checkAndRemoveUnuseObservers();
        boolean isListenerExists = false;
        for (WeakReference<T> ref : mListenerRefs) {
            if (observer == ref.get()) {
                isListenerExists = true;
                break;
            }
        }
        if (!isListenerExists) {
            mListenerRefs.add(new WeakReference<>(observer));
        }
    }

    public final void unregister(T observer) {
        if (observer == null) {
            return;
        }
        checkAndRemoveUnuseObservers();
        int len = mListenerRefs.size();
        for (int i = len - 1; i >= 0; i--) {
            WeakReference<T> ref = mListenerRefs.get(i);
            if (observer == ref.get()) {
                mListenerRefs.remove(i);
                break;/**找到后终止 li 2015-01-23**/
            }
        }
    }

    public final List<WeakReference<T>> getList() {
        checkAndRemoveUnuseObservers();
        return mListenerRefs;
    }

    public final void checkAndRemoveUnuseObservers() {
        int len = mListenerRefs.size();
        for (int i = len - 1; i >= 0; i--) {
            WeakReference<T> ref = mListenerRefs.get(i);
            if (ref == null || ref.get() == null) {
                mListenerRefs.remove(i);
            }
        }
    }
}
