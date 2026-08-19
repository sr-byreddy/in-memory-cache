package com.byreddy.cache;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe cache that stores entries in the current process's memory.
 *
 * <p>Keys and values must be non-null. Entries remain in the cache until they
 * are explicitly deleted or replaced.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public final class InMemoryCache<K, V> implements Cache<K, V> {

    private final ConcurrentMap<K, V> entries = new ConcurrentHashMap<>();

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(entries.get(requireKey(key)));
    }

    @Override
    public void set(K key, V value) {
        entries.put(requireKey(key), Objects.requireNonNull(value, "value must not be null"));
    }

    @Override
    public boolean delete(K key) {
        return entries.remove(requireKey(key)) != null;
    }

    private K requireKey(K key) {
        return Objects.requireNonNull(key, "key must not be null");
    }
}
