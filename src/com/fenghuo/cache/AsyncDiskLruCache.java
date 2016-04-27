package com.fenghuo.cache;

import android.os.Looper;
import android.os.Message;

import com.fenghuo.diskLruCache.DiskLruCache;
import com.fenghuo.handler.Handler;
import com.fenghuo.handler.ISender;
import com.fenghuo.thread.ThreadPool;

import java.io.File;
import java.io.IOException;

/**
 * Created with Android Studio
 * <p/>
 * Project: lockscreen
 * Author: zhangshaolin(www.iooly.com)
 * Date:   14-6-26
 * Time:   上午7:46
 * Email:  app@iooly.com
 */
public class AsyncDiskLruCache<V> implements Handler {

    private static final String TASK_ID_PREFIX = "AsyncDiskLruCache://";

    private final DiskLruCache mDiskCache;
    private final ThreadPool mThreadPool;
    private final ISender mSender = ISender.Factory.newSender(this, Looper.getMainLooper());

    public static AsyncDiskLruCache open(File directory, int appVersion, int valueCount,
                                         long maxSize) throws IOException {
        DiskLruCache cache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);
        return new AsyncDiskLruCache(cache);
    }

    private AsyncDiskLruCache(DiskLruCache cache)
            throws IOException {
        mDiskCache = cache;
        mThreadPool = ThreadPool.getInstance();
    }

    public void get(String key) {

    }

    @Override
    public void handleMessage(Message msg) {

    }
}
