package com.sample.glide;

import android.util.Log;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.cache.LruResourceCache;

public class CustomLruResourceCache extends LruResourceCache {

    private final static String TAG = "ResourceCache|Glide";

    private volatile int hitCount;
    private volatile int missCount;

    public CustomLruResourceCache(int size) {
        super(size);
    }

    @Override
    public Resource<?> put(Key key, Resource<?> item) {
        Log.d(TAG, "put");
        return super.put(key, item);
    }

    @Override
    public Resource<?> get(Key key) {
        Log.d(TAG, "get");
        return super.get(key);
    }

    @Override
    public boolean contains(Key key) {
        Log.d(TAG, "contains");
        return super.contains(key);
    }

    @Override
    public Resource<?> remove(Key key) {
        final Resource<?> item = super.remove(key);
        if (item == null) {
            missCount += 1;
        } else {
            hitCount += 1;
        }
        Log.d(TAG, "remove:missCount:" + missCount + ",hitCount:" + hitCount);
        return item;
    }
}
