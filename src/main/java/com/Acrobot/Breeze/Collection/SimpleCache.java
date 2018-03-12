package com.Acrobot.Breeze.Collection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class SimpleCache<K, V> {
    private final LinkedHashMap<K, V> map;

    public SimpleCache(int cacheSize) {
        map = new LinkedHashMap<K, V>(cacheSize * 10/9, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > cacheSize;
            }
        };
    }

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public V get(K key, Callable<? extends V> loader) throws ExecutionException {
        if (contains(key)) {
            return map.get(key);
        }
        try {
            V value = loader.call();
            if (value != null) {
                put(key, value);
            }
            return value;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    private boolean contains(K key) {
        return map.containsKey(key);
    }
}
