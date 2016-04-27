package com.fenghuo.cache;

import android.os.Message;

import com.fenghuo.handler.Handler;
import com.fenghuo.handler.ISender;


public abstract class AbsAsyncCache<K, V> implements IAsyncCache<K, V>, Handler {

    protected final ISender mSender = ISender.Factory.newSender(this);

    @Override
    public V get(K key) {
        getSync(key);
        return null;
    }

    @Override
    public void handleMessage(Message msg) {
        AsyncData data = (AsyncData) msg.obj;
        @SuppressWarnings("unchecked")
        K key = (K) data.getKey();
        @SuppressWarnings("unchecked")
        V value = (V) data.getValue();
        data.recycle();
        data = null;
        notifyNewValue(key, value);
    }

    abstract protected void getSync(K key);

}
