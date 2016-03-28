package com.sample;

import android.app.Application;
import android.content.Context;

import com.sample.performance.ActivityLifeCycleTimeUseTracker;
import com.sample.performance.ViewHericacy;

/**
 * Created by clarkehe on 1/18/16.
 * Todo:
 */
public class App extends Application {

    public App() {
        super();
    }

    private static Context mAppContext;

    public static Context getContext() {
        return mAppContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = this;
        ActivityLifeCycleTimeUseTracker.getInstance().start();

        ViewHericacy.trackViewTreeDepth(this);
    }
}
