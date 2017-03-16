package org.skynetsoftware.dataloader;

import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pedja on 17.8.16. 15.32.
 * This class is part of the data-loader
 * Copyright © 2016 ${OWNER}
 * <p>
 * Read operations are done on a caller thread<br>
 * Write operations are done on a worker thread
 */

public class FSCache extends Cache
{
    private File mCacheLocation;
    private final ExecutorService mExecutorService;
    private final Kryo mKryo;

    protected FSCache()
    {
        try
        {
            Class.forName("com.esotericsoftware.kryo.Kryo");
        }
        catch (ClassNotFoundException e)
        {
            if(DEBUG)e.printStackTrace();
            throw new IllegalStateException("Kryo was not found in the classpath. To use FSCache you must add kryo as a dependencie");
        }
        mExecutorService = Executors.newCachedThreadPool();
        mKryo = new Kryo();
        mCacheLocation = new File("./fs_cache");
        boolean success = mCacheLocation.mkdir();
        if(!success)
        {
            Log.e(LOG_TAG, "Failed to create cache directory. Caching will not work");
        }
    }

    @Override
    protected <T> void put(final String key, final CacheObject<T> cacheObject)
    {
        mExecutorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                String filename = md5(key);
                File cacheFile = new File(mCacheLocation, filename);
                try
                {
                    Output output = new Output(new FileOutputStream(cacheFile));
                    mKryo.writeObject(output, cacheObject);
                    output.close();
                }
                catch (FileNotFoundException e)
                {
                    if (DEBUG) e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected <T> CacheObject<T> read(String key)
    {
        CacheObject<T> cacheObject = null;
        try
        {
            String filename = md5(key);
            File cacheFile = new File(mCacheLocation, filename);
            Input input = new Input(new FileInputStream(cacheFile));
            cacheObject = mKryo.readObject(input, CacheObject.class);
            input.close();
        }
        catch (FileNotFoundException e)
        {
            if (DEBUG) e.printStackTrace();
        }
        return cacheObject;
    }

    @Override
    protected <T> CacheObject<T> remove(String key)
    {
        String filename = md5(key);
        File cacheFile = new File(mCacheLocation, filename);
        boolean deleted = cacheFile.delete();
        if(!deleted && DEBUG)
        {
            Log.w(LOG_TAG, "FSCache::remove(key) - failed to delete cache with key: " + key);
        }
        return null;//TODO cant return object here
    }

    @Override
    protected Set<String> keySet()
    {
        Set<String> keys = new HashSet<>();
        File[] files = mCacheLocation.listFiles();
        if (files != null)
            for (File file : files)
            {
                keys.add(file.getName());
            }
        return keys;
    }

    @Override
    public void flushCache()
    {
        int deleted = 0, failed = 0;
        File[] files = mCacheLocation.listFiles();
        if (files != null)
            for (File file : files)
            {
                boolean success = file.delete();
                if(success)
                    deleted++;
                else
                    failed++;
            }
        if(DEBUG)
        {
            Log.i(LOG_TAG, String.format("deleted: '%d', failed: '%d'", deleted, failed));
        }
    }

    private String md5(final String s)
    {
        final String MD5 = "MD5";
        try
        {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
            {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            String log = "StringUtils.md5 >> error: " + e.getMessage();
            if (DEBUG) Log.w(LOG_TAG, log);
        }
        return "";
    }
}
