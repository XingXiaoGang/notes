package com.fenghuo.cache;

import android.support.v4.util.Pools;

public class AsyncData {

    private static final int MAX_POOL_SIZE = 10;
    private static final Pools.Pool<AsyncData> sPool = new Pools.SynchronizedPool<AsyncData>(MAX_POOL_SIZE);

    public Object key;
    public Object value;

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public static AsyncData obtain(Object key, Object value) {
        AsyncData instance = sPool.acquire();
        if (instance != null) {
            instance.setData(key, value);
        } else {
            instance = new AsyncData(key, value);
        }
        return instance;
    }

    AsyncData(Object key, Object value) {
        setData(key, value);
    }

    private void setData(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public void recycle() {
        key = null;
        value = null;
        try {
            sPool.release(this);
        } catch (Exception ex) {
        }
    }
}