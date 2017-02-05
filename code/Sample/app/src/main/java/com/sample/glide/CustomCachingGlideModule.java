package com.sample.glide;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.StringLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

public class CustomCachingGlideModule implements GlideModule {

    private final static String TAG = "GlideModule|Glide";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        Log.d(TAG, "defaultMemoryCacheSize:" + defaultMemoryCacheSize);
        Log.d(TAG, "defaultBitmapPoolSize:" + defaultBitmapPoolSize);

        //int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        //int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        //defaultMemoryCacheSize = 0;
        builder.setMemoryCache(new CustomLruResourceCache(defaultMemoryCacheSize));
        builder.setBitmapPool(new CustomLruBitmapPool(defaultBitmapPoolSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(String.class, InputStream.class, new CustomImageSizeModelFactory());
    }

    private class CustomImageSizeUrlLoader extends StringLoader<InputStream> implements StreamModelLoader<String> {
        @Override
        public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
            Log.d(TAG, "mode:" + model);
            Log.d(TAG, "width:" + width + ",height:" + height);
            return super.getResourceFetcher(model, width, height);
        }

        public CustomImageSizeUrlLoader(ModelLoader<Uri, InputStream> uriLoader) {
            super(uriLoader);
        }
    }

    private class CustomImageSizeModelFactory implements ModelLoaderFactory<String, InputStream> {
        @Override
        public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new CustomImageSizeUrlLoader(factories.buildModelLoader(Uri.class, InputStream.class));
        }

        @Override
        public void teardown() {
        }
    }
}