package com.sample.performance;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ActivityLifeCycleTracker {

    private boolean mCanReflect = false;
    private static volatile ActivityLifeCycleTracker sInstance;

    private ActivityLifeCycleTracker() {}

    public static ActivityLifeCycleTracker getInstance() {
        if (sInstance == null) {
            synchronized (ActivityLifeCycleTracker.class) {
                if (sInstance == null) {
                    sInstance = new ActivityLifeCycleTracker();
                }
            }
        }
        return sInstance;
    }

    public static abstract class EnhanceActivityLifeCycleCallback {
        public void onBeforeActivityCreate(Activity activity, Bundle savedInstanceState) {
        }

        public void onAfterActivityCreate(Activity activity, Bundle savedInstanceState) {
        }

        public void onBeforeActivityResume(Activity activity) {
        }

        public void onAfterActivityResume(Activity activity) {
        }

        public void onBeforeActivityPause(Activity activity) {
        }

        public void onAfterActivityPause(Activity activity) {
        }

        public void onBeforeActivityStart(Activity activity) {
        }

        public void onAfterActivityStart(Activity activity) {
        }

        public void onBeforeActivityRestart(Activity activity) {
        }

        public void onAfterActivityRestart(Activity activity) {
        }

        public void onBeforeActivityNewIntent(Activity activity, Intent intent) {
        }

        public void onAfterActivityNewIntent(Activity activity, Intent intent) {
        }

        public void onBeforeActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onAfterActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onBeforeActivityRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        }

        public void onAfterActivityRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        }

        public void onBeforeActivityStop(Activity activity) {
        }

        public void onAfterActivityStop(Activity activity) {
        }

        public void onBeforeActivityDestroy(Activity activity) {
        }

        public void onAfterActivityDestroy(Activity activity) {
        }
    }

    private Set<EnhanceActivityLifeCycleCallback> mCallbacks = new HashSet<EnhanceActivityLifeCycleCallback>();

    private class MyInstrumentation extends Instrumentation {
        @Override
        public void callActivityOnCreate(Activity activity, Bundle icicle) {
            notifyActivityOnCreate(activity, icicle, true);
            mOrigInstrumentation.callActivityOnCreate(activity, icicle);
            notifyActivityOnCreate(activity, icicle, false);
        }

        @Override
        public void callActivityOnResume(Activity activity) {
            notifyActivityOnResume(activity, true);
            mOrigInstrumentation.callActivityOnResume(activity);
            notifyActivityOnResume(activity, false);
        }

        @Override
        public void callActivityOnPause(Activity activity) {
            notifyActivityOnPause(activity, true);
            mOrigInstrumentation.callActivityOnPause(activity);
            notifyActivityOnPause(activity, false);
        }

        @Override
        public void callActivityOnStart(Activity activity) {
            notifyActivityOnStart(activity, true);
            mOrigInstrumentation.callActivityOnStart(activity);
            notifyActivityOnStart(activity, false);
        }

        @Override
        public void callActivityOnRestart(Activity activity) {
            notifyActivityOnRestart(activity, true);
            mOrigInstrumentation.callActivityOnRestart(activity);
            notifyActivityOnRestart(activity, false);
        }

        @Override
        public void callActivityOnNewIntent(Activity activity, Intent intent) {
            notifyActivityOnNewIntent(activity, intent, true);
            mOrigInstrumentation.callActivityOnNewIntent(activity, intent);
            notifyActivityOnNewIntent(activity, intent, false);
        }

        @Override
        public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
            notifyActivityOnSaveInstanceState(activity, outState, true);
            mOrigInstrumentation.callActivityOnSaveInstanceState(activity, outState);
            notifyActivityOnSaveInstanceState(activity, outState, false);
        }

        @Override
        public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
            notifyActivityOnRestoreInstanceState(activity, savedInstanceState, true);
            mOrigInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
            notifyActivityOnRestoreInstanceState(activity, savedInstanceState, false);
        }

        @Override
        public void callActivityOnStop(Activity activity) {
            notifyActivityOnStop(activity, true);
            mOrigInstrumentation.callActivityOnStop(activity);
            notifyActivityOnStop(activity, false);
        }

        @Override
        public void callActivityOnDestroy(Activity activity) {
            notifyActivityOnDestroy(activity, true);
            mOrigInstrumentation.callActivityOnDestroy(activity);
            notifyActivityOnDestroy(activity, false);
        }

    }

    private Object mActivityThreadObj = null;
    private Instrumentation mOrigInstrumentation = null;

    /**
     * 注册Activity生命周期事件的回调对象
     * 回调会运行在主进程
     *
     * @param callback 回调对象
     * @throws IllegalArgumentException, 当callback为null时
     */
    public synchronized void registerActivityLifeCycleCallback(EnhanceActivityLifeCycleCallback callback) {
        if (callback == null) throw new IllegalArgumentException("callback is null");
        if (!mCanReflect)
            return;
        mCallbacks.add(callback);
    }

    /**
     * 取消注册Activity生命周期事件的回调对象
     *
     * @param callback 回调对象
     * @throws IllegalArgumentException, 当callback为null时
     */
    public synchronized void unregisterActivityLifeCycleCallback(EnhanceActivityLifeCycleCallback callback) {
        if (callback == null) throw new IllegalArgumentException("callback is null");
        if (!mCanReflect)
            return;
        mCallbacks.remove(callback);
    }

    private synchronized void notifyActivityOnCreate(Activity activity, Bundle savedInstanceState, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityCreate(activity, savedInstanceState);
            } else {
                cb.onAfterActivityCreate(activity, savedInstanceState);
            }
        }
    }

    private synchronized void notifyActivityOnResume(Activity activity, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityResume(activity);
            } else {
                cb.onAfterActivityResume(activity);
            }
        }
    }

    private synchronized void notifyActivityOnPause(Activity activity, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityPause(activity);
            } else {
                cb.onAfterActivityPause(activity);
            }
        }
    }

    private synchronized void notifyActivityOnStart(Activity activity, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityStart(activity);
            } else {
                cb.onAfterActivityStart(activity);
            }
        }
    }

    private synchronized void notifyActivityOnRestart(Activity activity, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityRestart(activity);
            } else {
                cb.onAfterActivityRestart(activity);
            }
        }
    }

    private synchronized void notifyActivityOnNewIntent(Activity activity, Intent intent, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityNewIntent(activity, intent);
            } else {
                cb.onAfterActivityNewIntent(activity, intent);
            }
        }
    }

    private synchronized void notifyActivityOnSaveInstanceState(Activity activity, Bundle outState, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivitySaveInstanceState(activity, outState);
            } else {
                cb.onAfterActivitySaveInstanceState(activity, outState);
            }
        }
    }

    private synchronized void notifyActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityRestoreInstanceState(activity, savedInstanceState);
            } else {
                cb.onAfterActivityRestoreInstanceState(activity, savedInstanceState);
            }
        }
    }

    private synchronized void notifyActivityOnStop(Activity activity, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityStop(activity);
            } else {
                cb.onAfterActivityStop(activity);
            }
        }
    }

    private synchronized void notifyActivityOnDestroy(Activity activity, boolean isBefore) {
        Iterator<EnhanceActivityLifeCycleCallback> it = mCallbacks.iterator();
        while (it.hasNext()) {
            EnhanceActivityLifeCycleCallback cb = it.next();
            if (isBefore) {
                cb.onBeforeActivityDestroy(activity);
            } else {
                cb.onAfterActivityDestroy(activity);
            }
        }
    }

    public boolean startTrack() {
        boolean result = true;
        try {
            mActivityThreadObj = ReflectHelper.invokeMethod(
                    "android.app.ActivityThread",
                    "currentActivityThread",
                    null,
                    (Class<?>[]) null,
                    (Object[]) null
            );

            if (mActivityThreadObj == null)
                throw new IllegalStateException("Failed to get CurrentActivityThread.");

            mOrigInstrumentation = (Instrumentation) ReflectHelper.getField(
                    mActivityThreadObj.getClass(),
                    "mInstrumentation",
                    mActivityThreadObj
            );

            if (mOrigInstrumentation == null)
                throw new IllegalStateException("Failed to get Instrumentation instance.");


            //表示已经被hack过了，不需要再搞一次
            if (mOrigInstrumentation.getClass().equals(MyInstrumentation.class)) {
                return true;
            }

            if (!(mOrigInstrumentation.getClass().equals(Instrumentation.class))) {
                throw new IllegalStateException("Not original Instrumentation instance, give up monitoring.");
            }

            ReflectHelper.setField(
                    mActivityThreadObj.getClass(),
                    "mInstrumentation",
                    new MyInstrumentation(),
                    mActivityThreadObj
            );

        } catch (Exception e) {
            result = false;
        }
        mCanReflect = result;
        return result;

    }

    private void stopTrack() {
        ReflectHelper.setField(
                mActivityThreadObj.getClass(),
                "mInstrumentation",
                mOrigInstrumentation,
                mActivityThreadObj
        );
    }
}
