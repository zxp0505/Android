package com.sample;

import android.app.Application;
import android.content.Context;

import com.github.anrwatchdog.ANRWatchDog;
import com.sample.hotfix.dex.MultiDex;
import com.sample.performance.ActivityLifeCycleTimeUseTracker;
import com.sample.performance.ViewHericacy;

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
        new ANRWatchDog().start();

        ActivityLifeCycleTimeUseTracker.getInstance().start();
        ViewHericacy.trackViewTreeDepth(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this, "/data/local/tmp/test.dex");
    }
}
