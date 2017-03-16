package org.skynetsoftware.dataloader;

import android.util.Log;


/**
 * Created by pedja on 7.11.14. 09.34.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public class CacheDataProvider<CacheClass extends org.skynetsoftware.dataloader.Cache, Data> implements DataProvider<Data>
{
    private String mKey;
    private Data mResultData;
    private Class<CacheClass> mCacheClass;

    public CacheDataProvider(String key, Class<CacheClass> cacheClass)
    {
        this.mKey = key;
        this.mCacheClass = cacheClass;
    }

    @Override
    public boolean load()
    {
        if (DataLoader.DEBUG)
            Log.d(DataLoader.LOG_TAG, String.format("CacheDataProvider::load()[mKey=%s]", mKey));
        mResultData = org.skynetsoftware.dataloader.Cache.getInstance(mCacheClass).getFromCache(mKey);
        return mResultData != null;
    }

    @Override
    public Data getResult()
    {
        return mResultData;
    }

    @Override
    public boolean forceLoading()
    {
        return false;
    }

    @Override
    public Object getMetadata()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "CacheDataProvider{" +
                "mKey='" + mKey + '\'' +
                ", mCacheClass=" + mCacheClass +
                '}';
    }
}
