package org.skynetsoftware.dataloader;


import android.os.Handler;
import android.os.Looper;

import java.util.List;

/**
 * Created by pedja on 6.11.14. 15.39.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public class AndroidDataLoader<T> extends DataLoader<T>
{
    private Handler mainLoopHandler;

    /**
     * @param listener optional {@link AndroidDataLoader.LoadListener}
     * @param providers List of {@link DataProvider}s.<br>
     *                  Providers will be used in order they are placed in the list.<br>
     *                 */
    public AndroidDataLoader(LoadListener<T> listener, List<DataProvider<T>> providers)
    {
        super(listener, providers);
        mainLoopHandler = new Handler(Looper.getMainLooper());
    }

    public AndroidDataLoader()
    {
        this(null, null);
    }

    public AndroidDataLoader(List<DataProvider<T>> dataProviders)
    {
        this(null, dataProviders);
    }

    @Override
    protected void runOnUiThread(Runnable runnable)
    {
        mainLoopHandler.post(runnable);
    }
}
