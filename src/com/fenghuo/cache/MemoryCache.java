package com.fenghuo.cache;

import android.support.v4.util.LruCache;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.LinkedHashMap;

public class MemoryCache<K, V> implements ICache<K, V> {

    private final LruCache<K, V> mCache;


    private final LinkedHashMap<K, SoftCacheEntry<K, V>> mSoftCache = new LinkedHashMap<K, SoftCacheEntry<K, V>>();
    private final ReferenceQueue<V> mSoftCacheReferenceQueue = new ReferenceQueue<V>();

    private final Object mSync = new Object();
    private Monitor<V> mMonitorList;
    private final ReferenceQueue<V> mMonitorReferenceQueue = new ReferenceQueue<>();

    public MemoryCache(boolean useSoftCache, int maxSize) {
        mCache = new LruCache<K, V>(maxSize) {

            @Override
            protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
                if (evicted) {
                    putIntoSoftCache(key, oldValue);
                } else {
                    onRemoveInternal(key, oldValue);
                }
            }

        };
    }

    public MemoryCache(int maxSize) {
        this(true, maxSize);
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        V value = mCache.get(key);
        pollSoftCache();
        if (value == null) {
            synchronized (mSync) {
                SoftCacheEntry<K, V> entry = mSoftCache.get(key);
                if (entry != null) {
                    value = entry.get();
                }
            }
        }
        pollMonitor();
        return value;
    }

    @Override
    public void put(K key, V value) {
        if (key != null && value != null) {
            // 先从softCache中把数据移除， 防止cache一put就马上被放到weakCache， 而weakCache又把数据移除了
            synchronized (mSync) {
                SoftCacheEntry<K, V> entry = mSoftCache.remove(key);
                if (entry != null) {
                    onRemoveInternal(entry.key, entry.get());
                }
            }
            mCache.put(key, value);
            putIntoMonitor(value);
        }
        pollSoftCache();
        pollMonitor();
    }

    @Override
    public void clear() {
        mCache.evictAll();
        synchronized (mSync) {
            Collection<SoftCacheEntry<K, V>> values = mSoftCache.values();
            for (SoftCacheEntry<K, V> entry : values) {
                if (entry != null) {
                    onRemoveInternal(entry.key, entry.get());
                }
            }
            mSoftCache.clear();
            values.clear();
        }
        pollMonitor();
    }

    @Override
    public void remove(K key) {
        if (key != null) {
            mCache.remove(key);
            synchronized (mSync) {
                SoftCacheEntry<K, V> entry = mSoftCache.remove(key);
                if (entry != null) {
                    onRemoveInternal(entry.key, entry.get());
                }
            }
        }
        pollSoftCache();
        pollMonitor();
    }

    @Override
    public int size() {
        pollSoftCache();
        int size;
        synchronized (mSync) {
            size = mSoftCache.size();
        }
        return mCache.size() + size;
    }

    public int monitorSize() {
        pollMonitor();
        synchronized (mSync) {
            int size = 0;
            Monitor<V> monitor = mMonitorList;
            if (monitor != null) {
                do {
                    size++;
                } while ((monitor = monitor.prev) != null);
            }
            return size;
        }
    }

    private void putIntoSoftCache(K key, V value) {
        synchronized (mSync) {
            mSoftCache.put(key, new SoftCacheEntry<>(key, value, mSoftCacheReferenceQueue));
        }
    }

    private void pollSoftCache() {
        synchronized (mSync) {
            SoftCacheEntry<K, V> entry;
            K key;
            V value;
            while ((entry = (SoftCacheEntry<K, V>) mSoftCacheReferenceQueue.poll()) != null) {
                key = entry.key;
                value = entry.get();
                mSoftCache.remove(key);
                onRemoveInternal(key, value);
            }
        }
    }

    private void putIntoMonitor(V value) {
        synchronized (mSync) {
            if (mMonitorList == null) {
                mMonitorList = new Monitor<>(value, mMonitorReferenceQueue);
            } else {
                mMonitorList.next = new Monitor<>(value, mMonitorReferenceQueue);
                mMonitorList.next.prev = mMonitorList;
                mMonitorList = mMonitorList.next;
            }

        }
    }

    private void pollMonitor() {
        synchronized (mSync) {
            Monitor<V> reference;
            while ((reference = (Monitor<V>) mMonitorReferenceQueue.poll()) != null) {
                if (reference.next != null) {
                    reference.next.prev = reference.prev;
                }
                if (reference.prev != null) {
                    reference.prev.next = reference.next;
                }

                reference.prev = null;
                reference.next = null;

                if (mMonitorList == reference) {
                    mMonitorList = null;
                }
            }
        }
    }

    private void onRemoveInternal(K key, V value) {
        onRemove(key, value);
        pollMonitor();
    }

    protected void onRemove(K key, V value) {
    }

    private static class Monitor<T> extends PhantomReference<T> {

        private Monitor<T> next;
        private Monitor<T> prev;

        /**
         * Constructs a new phantom reference and registers it with the given
         * reference queue. The reference queue may be {@code null}, but this case
         * does not make any sense, since the reference will never be enqueued, and
         * the {@link #get()} method always returns {@code null}.
         *
         * @param r the referent to track
         * @param q the queue to register the phantom reference object with
         */
        public Monitor(T r, ReferenceQueue<? super T> q) {
            super(r, q);
        }
    }

    private static class SoftCacheEntry<K, V> extends SoftReference<V> {

        public final K key;

        public SoftCacheEntry(K key, V value, ReferenceQueue<? super V> q) {
            super(value, q);
            this.key = key;
        }
    }

}
