package com.androidforever.dataloader.lib;

/**
 * Created by pedja on 6.11.14. 15.41.
 * This class is part of the data-loader
 * Copyright © 2014 Predrag Čokulov
 */
public interface DataProvider<T>
{
    public static final int REQUEST_CODE_SOMETHING = 401;
    public boolean load();
    public T getResult();
}
