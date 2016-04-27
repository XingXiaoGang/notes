
package com.fenghuo.cache;

public interface AsyncCacheListener<K, V> {
    void onValueLoaded(K key, V value);
}
