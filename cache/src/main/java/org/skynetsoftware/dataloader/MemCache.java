package org.skynetsoftware.dataloader;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pedja on 5.11.14. 16.06.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public class MemCache extends Cache
{
    private Map<String, SoftReference<CacheObject>> cache;

    protected MemCache()
    {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    protected <T> void put(String key, CacheObject<T> cacheObject)
    {
        cache.put(key, new SoftReference<CacheObject>(cacheObject));
    }

    @Override
    protected <T> CacheObject<T> read(String key)
    {
        return cache.get(key) == null ? null : cache.get(key).get();
    }

    @Override
    protected <T> CacheObject<T> remove(String key)
    {
        SoftReference<CacheObject> ref = cache.remove(key);
        return ref == null ? null : ref.get();
    }

    @Override
    protected Set<String> keySet()
    {
        return cache.keySet();
    }

    @Override
    public void flushCache()
    {
        cache.clear();
    }
}
