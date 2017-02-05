package com.sample.glide;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;

public class CustomLruBitmapPool extends LruBitmapPool {

    private final static String TAG = "BitmapPool|Glide";

    private volatile int hitCount;
    private volatile int missCount;

    public CustomLruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    public synchronized Bitmap get(int width, int height, Bitmap.Config config) {
        Log.d(TAG, "get, width:" + width + ",height:" + height);
        final Bitmap bmp = super.get(width, height, config);
        if (bmp != null) {
            hitCount += 1;
        } else {
            missCount += 1;
        }
        Log.d(TAG, "get:missCount:" + missCount + ",hitCount:" + hitCount);
        return bmp;
    }

    @Override
    public synchronized boolean put(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        Log.d(TAG, "put, width:" + width + ",height:" + height);
        return super.put(bitmap);
    }
}
