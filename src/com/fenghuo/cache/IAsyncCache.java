
package com.fenghuo.cache;

public interface IAsyncCache<K, V> extends ICache<K, V> {

    void setAsyncCacheListener(AsyncCacheListener<K, V> l);

    void notifyNewValue(K key, V value);

}
