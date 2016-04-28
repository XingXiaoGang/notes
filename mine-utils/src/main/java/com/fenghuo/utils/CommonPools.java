package com.fenghuo.utils;

import android.os.Bundle;
import android.support.v4.util.Pools;

/**
 * Created by 张少林
 * on 15-3-26 下午10:43
 * Email:zsl@kuba.bz
 * Android Studio
 * Function: 各类常用的池
 */
public class CommonPools {

    /**
     * StringBuilder Pool
     */

    public static StringBuilder obtainStringBuilder() {
        return new StringBuilder();
    }

    public static void recycle(StringBuilder stringBuilder) {
    }

    /**
     * byte[] pool, 主要用于文件，网络等流的缓存
     */
    private static final Pools.Pool<byte[]> BYTE_BUFFER_POOL = new Pools.SynchronizedPool<>(10);

    public static byte[] obtainByteBuffer() {
        byte[] instance = BYTE_BUFFER_POOL.acquire();
        instance = instance == null ? new byte[8192] : instance;
        return instance;
    }

    public static void recycle(byte[] cache) {
        if (cache != null) {
            try {
                BYTE_BUFFER_POOL.release(cache);
            } catch (Exception ex) {
            }
        }
    }

    /**
     * char[] pool, 主要用于文件，网络等流的缓存
     */
    private static final Pools.Pool<char[]> CHAR_BUFFER_POOL = new Pools.SynchronizedPool<>(10);

    public static char[] obtainCharBuffer() {
        char[] instance = CHAR_BUFFER_POOL.acquire();
        instance = instance == null ? new char[8192] : instance;
        return instance;
    }

    public static void recycle(char[] buffer) {
        if (buffer != null) {
            try {
                CHAR_BUFFER_POOL.release(buffer);
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Bundle pool
     */
    private static final Pools.Pool<Bundle> BUNDLE_BUFFER_POOL = new Pools.SynchronizedPool<>(10);

    public static Bundle obtainBundle() {
        Bundle instance = BUNDLE_BUFFER_POOL.acquire();
        instance = instance == null ? new Bundle() : instance;
        return instance;
    }

    public static void recycle(Bundle bundle) {
        if (bundle != null) {
            try {
                bundle.clear();
                BUNDLE_BUFFER_POOL.release(bundle);
            } catch (Exception ex) {
            }
        }
    }
}
