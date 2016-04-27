package com.fenghuo.cache;

import android.os.Looper;
import android.os.Message;

import com.fenghuo.handler.Handler;
import com.fenghuo.handler.ISender;
import com.fenghuo.observer.MultiObserver;
import com.fenghuo.observer.Observer;
import com.fenghuo.thread.PoolTask;
import com.fenghuo.thread.ThreadPool;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created with Android Studio
 * <p/>
 * Project: themelauncher
 * Author: zhangshaolin
 * Date:   14/12/12
 * Time:   下午6:34
 * Email:  zsl@kuba.bz
 */
public abstract class AsyncMemoryCache<K, V> extends MemoryCache<K, V>
        implements Observer<AsyncCacheListener<K, V>>, Handler, AsyncCacheListener<K, V> {

    private static final int WHAT_ON_VALUE_LOADED = 1;

    private final MultiObserver<AsyncCacheListener<K, V>> mObserver;
    private final ISender mSender = ISender.Factory.newSender(this, Looper.getMainLooper());

    private boolean isTaskRemovable = false;

    public AsyncMemoryCache(int maxSize) {
        this(true, maxSize);
    }

    public AsyncMemoryCache(boolean useSoftCache, int maxSize) {
        super(useSoftCache, maxSize);
        mObserver = new MultiObserver<>();
    }

    public void setTaskRemovable(boolean isTaskRemovable) {
        this.isTaskRemovable = isTaskRemovable;
    }

    @Override
    public final V get(K key) {
        V value = super.get(key);
        if (key != null && value == null) {
            ThreadPool.getInstance().addTask(
                    new AsyncLoadTask<>(getTaskId(key), key, this, mSender, false, isTaskRemovable)
            );
        }
        return value;
    }

    public final void update(K key) {
        if (key != null) {
            ThreadPool.getInstance().addTask(
                    new AsyncLoadTask<>(getTaskId(key), key, this, mSender, true, isTaskRemovable)
            );
        }
    }

    protected abstract V getAsync(K key);

    protected V updateSync(K key) {
        return getAsync(key);
    }

    protected abstract String getTaskId(K key);

    @Override
    public final void register(AsyncCacheListener<K, V> observer) {
        mObserver.register(observer);
    }

    @Override
    public final void unregister(AsyncCacheListener<K, V> observer) {
        mObserver.unregister(observer);
    }

    @Override
    public final void onValueLoaded(K key, V value) {
        List<WeakReference<AsyncCacheListener<K, V>>> list = mObserver.getList();
        for (WeakReference<AsyncCacheListener<K, V>> lrf : list) {
            AsyncCacheListener<K, V> l = lrf.get();
            if (l != null) {
                l.onValueLoaded(key, value);
            }
        }
    }

    @Override
    public final void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_ON_VALUE_LOADED: {
                AsyncData data = (AsyncData) msg.obj;
                if (data != null && data.getKey() != null && data.getValue() != null) {
                    K key = (K) data.getKey();
                    V value = (V) data.getValue();
                    put(key, value);
                    onValueLoaded(key, value);
                }
                if (data != null) {
                    data.recycle();
                }
                break;
            }
        }
    }

    private static class AsyncLoadTask<K, V> extends PoolTask {

        private final K mKey;
        private final WeakReference<AsyncMemoryCache<K, V>> mCacheReference;
        private final ISender mSender;
        private final boolean isUpdate;

        public AsyncLoadTask(String id, K key, AsyncMemoryCache<K, V> cache, ISender sender, boolean isUpdate, boolean removable) {
            super(id, removable);
            this.isUpdate = isUpdate;
            mKey = key;
            mCacheReference = new WeakReference<>(cache);
            mSender = sender.getReference();
        }

        @Override
        public void run() {
            AsyncMemoryCache<K, V> cache = mCacheReference.get();
            if (cache != null) {
                V value;
                if (isUpdate) {
                    value = cache.updateSync(mKey);
                } else {
                    value = cache.getAsync(mKey);
                }
                AsyncData data = AsyncData.obtain(mKey, value);
                Message msg = mSender.obtainMessage(WHAT_ON_VALUE_LOADED, data);
                if (msg != null) {
                    msg.sendToTarget();
                }
            }

        }
    }
}
