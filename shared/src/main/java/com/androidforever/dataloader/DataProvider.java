package com.androidforever.dataloader;

/**
 * Created by pedja on 6.11.14. 15.41.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public interface DataProvider<T>
{
    /**
     * Main data loading is done in this method<br>
     * This method will be called by {@link DataLoader } for each {@link DataProvider} until one of them returns true<br>
     * Make sure that you publish the loading result so that it is returned in {@link #getResult()}<br>
     * Usually if return is true, result shouldn't be null
     * @return true if load was successful, false if not*/
    boolean load();

    /**
     * This method should return loading result if {@link #load()} returned true*/
    T getResult();

    /**
     * You can override this method to force {@link DataLoader} to load this provider even if a previous provider returned true<br>
     * This only works for async {@link DataLoader}<br><br>
     *
     * Example usage:
     * <pre>
     *     Lets imagine for a second that we have an News application which should work offline.
     *     When user starts the application, you first want to show the old content to the user (stored in offline database),
     *     and then load new content from the internet
     *     When loading from net is done you show the new content
     *     In this case we could have 2 {@link DataProvider}s
     *      1. DatabaseProvider - loads news from database
     *      2. NetworkProvider - loads news from internet
     *
     *     Now if we add this two providers to DataLoader, it will first try to load data from DatabaseProvider and if loading is successful,
     *     it will stop loading and return the result.
     *
     *     But we can override {@link #forceLoading()} in NetworkProvider to return true, and this way DataLoader will also load data from NetworkProvider even if data was successfully loaded in DatabaseProvider
     * </pre>*/
    boolean forceLoading();

}
