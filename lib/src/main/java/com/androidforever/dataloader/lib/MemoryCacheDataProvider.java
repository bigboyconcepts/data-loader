package com.androidforever.dataloader.lib;

import android.util.Log;

/**
 * Created by pedja on 7.11.14. 09.34.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public class MemoryCacheDataProvider<T> implements DataProvider<T>
{
    String key;
    T resultData;

    public MemoryCacheDataProvider(String key)
    {
        this.key = key;
    }

    @Override
    public boolean load()
    {
        if (DataLoader.DEBUG)
            Log.d(DataLoader.LOG_TAG, String.format("MemoryCacheDataProvider::load()[key=%s]", key));
        resultData = MemCache.getInstance().getFromCache(key);
        return resultData != null;
    }

    @Override
    public T getResult()
    {
        return resultData;
    }
}
