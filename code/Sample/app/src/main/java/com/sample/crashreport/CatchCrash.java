package com.sample.crashreport;

import android.util.Log;

public class CatchCrash {
    private final static String TAG = "CatchCrash";
    private static Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    public static void catchUnhandledException() {
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
    }

    private static class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e(TAG, "***-------------");
            ex.printStackTrace();
            Log.e(TAG, "uncaughtException, thread:" + thread.getName() + ", ex:" + ex.toString());
            Log.e(TAG, "***-------------");
            defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    }
}
