package com.androidforever.dataloader;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by pedja on 3/13/16.
 */
public interface DataLoaderFactory
{
    @ObjectiveCName("newDataLoader:")
    <T> DataLoader<T> newDataLoader(Class type);
}
