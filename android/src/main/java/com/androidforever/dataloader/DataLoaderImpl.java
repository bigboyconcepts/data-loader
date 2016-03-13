package com.androidforever.dataloader;


import android.os.Handler;
import android.os.Looper;

import java.util.List;

/**
 * Created by pedja on 6.11.14. 15.39.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public class DataLoaderImpl<T> extends DataLoader<T>
{
    private Handler mainLoopHandler;

    /**
     * @param listener optional {@link DataLoaderImpl.LoadListener}
     * @param providers List of {@link DataProvider}s.<br>
     *                  Providers will be used in order they are placed in the list.<br>
     *                 */
    public DataLoaderImpl(LoadListener<T> listener, List<DataProvider<T>> providers)
    {
        super(listener, providers);
        mainLoopHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void runOnUiThread(Runnable runnable)
    {
        mainLoopHandler.post(runnable);
    }
}
