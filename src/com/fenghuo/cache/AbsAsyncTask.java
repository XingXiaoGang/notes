package com.fenghuo.cache;

import android.os.Message;

import com.fenghuo.handler.ISender;
import com.fenghuo.thread.PoolTask;


public abstract class AbsAsyncTask<K, V> extends PoolTask {

    final ISender mSender;
    final K mKey;

    public AbsAsyncTask(ISender sender, K key, String id, boolean removable) {
        super(id, removable);
        mSender = sender.getReference();
        mKey = key;
    }


    public AbsAsyncTask(ISender sender, K key, String id) {
        super(id, true);
        mSender = sender.getReference();
        mKey = key;
    }

    @Override
    public void run() {
        V value = get(mKey);
        sendValue(mKey, value);
    }

    abstract V get(K key);

    protected void sendValue(K key, V value) {
        AsyncData data = AsyncData.obtain(key, value);
        Message msg = Message.obtain();
        if (msg != null) {
            msg.obj = data;
            mSender.sendMessage(msg);
        }
    }

}