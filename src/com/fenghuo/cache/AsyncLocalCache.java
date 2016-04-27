package com.fenghuo.cache;

import android.app.Application;
import android.os.Environment;

import com.fenghuo.diskLruCache.DiskLruCache;
import com.fenghuo.utils.FileUtils;
import com.fenghuo.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 张少林
 * on 15-2-2 下午3:19
 * Email:zsl@kuba.bz
 * Android Studio
 * Function:
 */
public abstract class AsyncLocalCache<K, V> extends AsyncMemoryCache<K, V> {

    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private final Object mDiskCacheSync = new Object();

    private DiskLruCache mDiskLruCache;

    private Application mApplication;

    private final long mMaxDiskCacheSizeInBytes;

    public AsyncLocalCache(int memoryCacheMaxSize, long maxDiskCacheSizeInBytes, Application application) {
        this(true, memoryCacheMaxSize, maxDiskCacheSizeInBytes, application);
    }

    public AsyncLocalCache(boolean useSoftCache, int memoryCacheMaxSize, long maxDiskCacheSizeInBytes, Application application) {
        super(useSoftCache, memoryCacheMaxSize);
        this.mApplication = application;
        mMaxDiskCacheSizeInBytes = maxDiskCacheSizeInBytes;
        mDiskLruCache = newDiskCache();
    }

    public Application getApplication() {
        return mApplication;
    }

    @Override
    protected final V getAsync(K key) {
        DiskLruCache cache = getDiskCache();
        V value = getValueFromCache(cache, key);
        if (value == null) {
            value = getValueFromRemote(key);
        }
        return value;
    }

    protected abstract InputStream openRemoteStream(K key) throws IOException;

    protected abstract V onDecodeValue(InputStream in, K key);

    protected abstract String keyToString(K key);

    private String keyToStringInternal(K key) {
        String stringKey = keyToString(key);

        if (stringKey == null) {
            throw new NullPointerException("keyToString must return a not null value");
        }

        DiskLruCache.validateKey(stringKey);

        return stringKey;
    }

    private V getValueFromCache(DiskLruCache cache, K key) {
        synchronized (mDiskCacheSync) {
            return getValueFromCacheLocked(cache, key);
        }
    }

    private V getValueFromCacheLocked(DiskLruCache cache, K key) {
        V value = null;
        DiskLruCache.Snapshot snapshot = null;
        String stringKey = keyToStringInternal(key);
        try {
            snapshot = cache.get(stringKey);
            if (snapshot != null) {
                final InputStream in = snapshot.getInputStream(0);
                if (in != null) {
                    final BufferedInputStream buffIn =
                            new BufferedInputStream(in, IO_BUFFER_SIZE);
                    value = onDecodeValue(buffIn, key);
                }
            }
        } catch (Exception e) {
        } finally {
            if (snapshot != null) {
                try {
                    snapshot.close();
                } catch (Exception ex) {
                }
            }
        }
        return value;
    }

    public int memoryCacheSize() {
        return super.size();
    }

    public long diskSize() {
        return mDiskLruCache.size();
    }

    private void putValueIntoCacheLocked(DiskLruCache cache, K key, InputStream in) {
        DiskLruCache.Editor editor = null;
        String stringKey = keyToStringInternal(key);
        try {
            editor = cache.edit(stringKey);
            if (editor == null) {
                return;
            }

            if (writeValueToFile(in, editor)) {
                cache.flush();
                editor.commit();
            } else {
                editor.abort();
            }
        } catch (Exception e) {
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private boolean writeValueToFile(InputStream in, DiskLruCache.Editor editor)
            throws IOException {
        OutputStream out = null;
        boolean result = false;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
            result = IOUtils.copy(in, out, IO_BUFFER_SIZE) > 0;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return result;
    }

    protected final V updateSync(K key) {
        return getValueFromRemote(key);
    }

    private V getValueFromRemote(K key) {

        V value = null;
        InputStream in = null;
        try {
            in = openRemoteStream(key);
            if (in != null) {
                synchronized (mDiskCacheSync) {
                    DiskLruCache cache = getDiskCache();
                    putValueIntoCacheLocked(cache, key, in);
                    value = getValueFromCacheLocked(cache, key);
                }
            }
        } catch (Exception ex) {
        } finally {
            FileUtils.close(in);
        }
        return value;
    }

    @Override
    protected final String getTaskId(K key) {
        return Integer.toHexString(hashCode()) + "::" + keyToString(key);
    }

    protected final boolean isSdcardOk() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    protected abstract String getCacheDirPath();

    private DiskLruCache getDiskCache() {
        synchronized (mDiskCacheSync) {
            if (!checkCacheLocked(mDiskLruCache)) {
                mDiskLruCache = newDiskCache();
            }
            return mDiskLruCache;
        }
    }

    private boolean checkCacheLocked(DiskLruCache cache) {
        return !(cache == null || cache.isClosed()) && FileUtils.isDirectory(cache.getDirectory());
    }

    private DiskLruCache newDiskCache() {
        File dir = new File(getCacheDirPath());
        DiskLruCache cache = null;
        try {
            if (FileUtils.checkDir(dir)) {
                cache = DiskLruCache.open(dir, APP_VERSION, VALUE_COUNT, mMaxDiskCacheSizeInBytes);
            }
        } catch (Exception ex) {
            cache = null;
        }
        return cache;
    }
}
