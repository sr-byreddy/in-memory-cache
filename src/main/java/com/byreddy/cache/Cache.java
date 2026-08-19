package com.byreddy.cache;

import java.util.Optional;

/**
 * Defines the basic operations supported by a cache.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface Cache<K, V> {

    /**
     * Retrieves the value associated with a key.
     *
     * @param key the key to look up
     * @return the associated value, or an empty optional when the key is absent
     */
    Optional<V> get(K key);

    /**
     * Associates a value with a key, replacing any existing value.
     *
     * @param key the key to store
     * @param value the value to store
     */
    void set(K key, V value);

    /**
     * Removes the value associated with a key.
     *
     * @param key the key to remove
     * @return {@code true} when an entry was removed, otherwise {@code false}
     */
    boolean delete(K key);
}
