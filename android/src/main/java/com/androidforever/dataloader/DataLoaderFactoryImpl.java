package com.androidforever.dataloader;

/**
 * Created by pedja on 3/13/16.
 */
public class DataLoaderFactoryImpl implements DataLoaderFactory
{
    @Override
    public <T> DataLoader<T> newDataLoader()
    {
        return new DataLoaderImpl<T>();
    }
}
