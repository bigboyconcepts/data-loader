package com.androidforever.dataloader;

/**
 * Created by pedja on 3/13/16.
 */
public class AndroidDataLoaderFactory implements DataLoaderFactory
{
    @Override
    public <T> DataLoader<T> newDataLoader()
    {
        return new AndroidDataLoader<T>();
    }
}
