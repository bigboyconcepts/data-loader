package org.skynetsoftware.dataloader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pedja on 5.11.14. 16.06.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 *
 *
 * <br>
 *
 * Implementation must have default constructor
 */
public abstract class Cache
{
    public static final String LOG_TAG = "data-loader-cache";
    public static boolean DEBUG = true;
    private static final long DEF_MAX_CACHE_AGE = 24 * 60 * 60 * 60 * 1000L;//24h
    private long instanceDefaultCacheAge = DEF_MAX_CACHE_AGE;

    private static Map<Class<? extends Cache>, Cache> instances;

    static
    {
        instances = new HashMap<>();
    }

    protected Cache()
    {

    }

    public static Cache getInstance(Class<? extends Cache> _class)
    {
        Cache instance;
        if((instance = instances.get(_class)) == null)
        {
            try
            {
                instance = _class.newInstance();
                instances.put(_class, instance);
            }
            catch (IllegalAccessException e)
            {
                if(DEBUG)e.printStackTrace();
            }
            catch (InstantiationException e)
            {
                if(DEBUG)e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Add object to cache.<br>
     * @param maxCacheAge max duration that this item will be 'valid' in ms*/
    public final <T> void addToCache(String key, T object, long maxCacheAge)
    {
        if(object == null || key == null)return;
        CacheObject<T> co = new CacheObject<>(System.currentTimeMillis(), object, maxCacheAge);
        put(key, co);
    }

    /**
     * Calls {@link #addToCache(String, T, long)} with default cache age value*/
    public final <T> void addToCache(String key, T object)
    {
        addToCache(key, object, instanceDefaultCacheAge);
    }

    /**
     * Remove object from with the given key*/
    public final void removeFromCache(String key)
    {
        remove(key);
    }

    /**
     * Remove all objects from cache that match pattern
     * This will iterate through cache keyset and check every key*/
    public final void removeFromCache(Pattern pattern)
    {
        Set<String> keys = keySet();
        for(String key : keys)
        {
            Matcher matcher = pattern.matcher(key);
            if(matcher.matches())
            {
                remove(key);
            }
        }
    }

    public final <T> T getFromCache(String key, Class<T> type)
    {
        CacheObject<T> co = read(key);
        if(co == null)return null;
        if(co.isExpired() || co.object == null)
        {
            removeFromCache(key);
            return null;
        }
        return co.object;
    }

    public final <T> T getFromCache(String key)
    {
        CacheObject<T> co = read(key);
        if(co == null)return null;
        if(co.isExpired() || co.object == null)
        {
            removeFromCache(key);
            return null;
        }
        return co.object;
    }

    /**
     * Set default cache age for all cache. Will be used if not implicitly specified in {@link CacheObject}*/
    public void setDefaultMaxCacheAge(long age)
    {
        instanceDefaultCacheAge = age;
    }

    /**
     * Implement this to put object to cache*/
    protected abstract <T> void put(String key, CacheObject<T> cacheObject);

    /**
     * Read object from cache*/
    protected abstract <T> CacheObject<T> read(String key);

    /**
     * Remove object from cache*/
    protected abstract <T> CacheObject<T> remove(String key);

    /**
     * Get all cache keys*/
    protected abstract Set<String> keySet();

    /**
     * Clear underlaying cache*/
    public abstract void flushCache();

    protected static class CacheObject<T>
    {
        long addedTs;
        T object;
        long maxCacheAge = 0;//Cache.getInstance().instanceDefaultCacheAge;

        public CacheObject()
        {
            //used by kryo
        }

        public CacheObject(long addedTs, T object)
        {
            this.addedTs = addedTs;
            this.object = object;
        }

        public CacheObject(long addedTs, T object, long maxCacheAge)
        {
            this.addedTs = addedTs;
            this.object =object;
            this.maxCacheAge = maxCacheAge;
        }

        public boolean isExpired()
        {
            return addedTs + maxCacheAge < System.currentTimeMillis();
        }

        @Override
        public String toString()
        {
            return "CacheObject{" +
                    "addedTs=" + addedTs +
                    ", object=" + object +
                    ", maxCacheAge=" + maxCacheAge +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(String key : keySet())
        {
            builder.append(key);
        }
        return builder.toString();
    }
}
