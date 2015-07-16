package com.androidforever.dataloader.lib;


import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

/**
 * Created by pedja on 6.11.14. 15.39.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public class DataLoader<T>
{
    public static final String LOG_TAG = "data-loader";

    public interface LoadListener<T>
    {
        /**
         * Called when {@link DataLoader} has finished loading data<br>
         * This is a good place to hide/dismiss any ProgressBar/ProgressDialog
         * @param status {@link Result#STATUS_OK} if any of the data loaders returned result, {@link Result#STATUS_ERROR} in any other case*/
        void onLoadingFinished(int status);

        /**
         * Called when data has been loaded<br>
         * This method could be called multiple times
         * @see DataProvider#forceLoading() */
        void onDataLoaded(Result<T> result);

        /**
         * Called before DataLoader starts to load data<br>
         * This is a good place to show any ProgressBar/ProgressDialog*/
        void onLoadStarted();
    }

    /**
     * This class holds load result
     * */
    public static class Result<T>
    {
        public static final int STATUS_OK = 1001;
        public static final int STATUS_ERROR = 1002;
        /**
         * Status of this result. It can be either {@link #STATUS_ERROR} or {@link #STATUS_OK}<br>
         * If the {@link DataLoader} was used asynchronously this will always be {@link #STATUS_OK}, and status will be delivered in {@link LoadListener#onLoadingFinished(int)}
         * @see LoadListener#onLoadingFinished(int) */
        public final int status;
        public final T data;
        public final DataProvider<T> provider;

        public Result(T data, int status, DataProvider<T> provider)
        {
            this.data = data;
            this.status = status;
            this.provider = provider;
        }
    }

    private LoadListener<T> listener;
    private List<DataProvider<T>> providers;
    private ATLoader atLoader;
    private boolean useThreadPoolExecutor;
    public static boolean DEBUG = true;

    private Handler mainLoopHandler;

    /**
     * @param listener optional {@link com.androidforever.dataloader.lib.DataLoader.LoadListener}
     * @param providers List of {@link DataProvider}s.<br>
     *                  Providers will be used in order they are placed in the list.<br>
     *                 */
    public DataLoader( LoadListener<T> listener,  List<DataProvider<T>> providers)
    {
        this.listener = listener;
        this.providers = providers;
        mainLoopHandler = new Handler(Looper.getMainLooper());
    }

    public DataLoader(List<DataProvider<T>> providers)
    {
        this(null, providers);
    }

    public DataLoader()
    {
        this(null, null);
    }

    public void setListener(LoadListener<T> listener)
    {
        this.listener = listener;
    }

    /**
     * List of providers for this loader<br>
     * Providers are used in order they are placed in a list, make sure that you place providers with higher priority first<br><br>*/
    public void setProviders(List<DataProvider<T>> providers)
    {
        this.providers = providers;
    }

    /**
     * whether to use {@link AsyncTask#THREAD_POOL_EXECUTOR} when executing tasks*/
    public void setUseThreadPoolExecutor(boolean useThreadPoolExecutor)
    {
        this.useThreadPoolExecutor = useThreadPoolExecutor;
    }

    /**
     * Start loading data<br><br>
     * This method will throw an exception if provider list is null or empty<br><br>
     * Loading is done on a worker thread, unless 'forceSerial' is true<br><br>
     * {@link DataProvider#load()} method will be called for
     * each provider until one of them returns true<br><br>
     * Providers are used in order they are placed in a list, make sure that you place providers with higher priority first<br><br>
     * Result will be delivered in {@link LoadListener#onDataLoaded(Result)}
     * @param forceSyncExecution force serial execution. Data loading will be executed on a caller thread
     * @return if forceSyncExecution is true returns result, if its false returns null*/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Result<T> loadData(boolean forceSyncExecution)
    {
        if(providers == null || providers.isEmpty())
        {
            throw new IllegalStateException("DataProvider array cannot be null or empty");
        }
        if (!forceSyncExecution)
        {
            atLoader = new ATLoader();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && useThreadPoolExecutor)
            {
                atLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else
            {
                atLoader.execute();
            }
        }
        else
        {
            for(DataProvider<T> provider : providers)
            {
                boolean success = provider.load();
                if(success)
                {
                    T result = provider.getResult();
                    return new Result<>(result, Result.STATUS_OK, provider);
                }
            }
            return new Result<>(null, Result.STATUS_ERROR, null);
        }
        return null;
    }

    public void loadData()
    {
        loadData(false);
    }

    private class ATLoader extends AsyncTask<String, Void, Integer>
    {
        @Override
        public Integer doInBackground(String... params)
        {
            boolean anySucceeded = false;
            for(DataProvider<T> provider : providers)
            {
                if(anySucceeded && !provider.forceLoading())
                    continue;
                boolean success = provider.load();
                if(success)
                {
                    T result = provider.getResult();
                    anySucceeded = true;
                    publishResult(new Result<>(result, Result.STATUS_OK, provider));
                }
            }
            return anySucceeded ? Result.STATUS_OK : Result.STATUS_ERROR;
        }

        @Override
        public void onPostExecute(Integer result)
        {
            if(listener != null)listener.onLoadingFinished(result);
        }

        @Override
        public void onPreExecute()
        {
            if(listener != null)listener.onLoadStarted();
        }

        private void publishResult(final Result<T> result)
        {
            mainLoopHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(listener != null)listener.onDataLoaded(result);
                }
            });
        }
    }

    /**
     * Cancel loading
     * This only calls cancel(true) on the underlying AsyncTask*/
    public void cancel()
    {
        if(atLoader != null)atLoader.cancel(true);
    }
}
