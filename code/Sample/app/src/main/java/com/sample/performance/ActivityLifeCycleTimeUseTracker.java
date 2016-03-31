package com.sample.performance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.sample.App;
import java.util.HashMap;

public class ActivityLifeCycleTimeUseTracker extends ActivityLifeCycleTracker.EnhanceActivityLifeCycleCallback {
    private static final String TAG = "ActivityLifeCycle";

    private static final int TIME_COST_WARNING = 350;

    private static volatile ActivityLifeCycleTimeUseTracker sInstance;

    public static ActivityLifeCycleTimeUseTracker getInstance() {
        if (sInstance == null) {
            synchronized (ActivityLifeCycleTimeUseTracker.class) {
                if (sInstance == null) {
                    sInstance = new ActivityLifeCycleTimeUseTracker();
                }
            }
        }
        return sInstance;
    }

    private HashMap<Integer, Long> mCreateTimeUsed;
    private HashMap<Integer, Long> mDestroyTimeUsed;
    private HashMap<Integer, Long> mNewIntentTimeUsed;
    private HashMap<Integer, Long> mPauseTimeUsed;
    private HashMap<Integer, Long> mResumeTimeUsed;
    private HashMap<Integer, Long> mStartTimeUsed;
    private HashMap<Integer, Long> mStopTimeUsed;

    private ActivityLifeCycleTimeUseTracker() {
        mCreateTimeUsed = new HashMap<Integer, Long>();
        mDestroyTimeUsed = new HashMap<Integer, Long>();
        mNewIntentTimeUsed = new HashMap<Integer, Long>();
        mPauseTimeUsed = new HashMap<Integer, Long>();
        mResumeTimeUsed = new HashMap<Integer, Long>();
        mStartTimeUsed = new HashMap<Integer, Long>();
        mStopTimeUsed = new HashMap<Integer, Long>();
    }

    public void start() {
        ActivityLifeCycleTracker.getInstance().startTrack();
        ActivityLifeCycleTracker.getInstance().registerActivityLifeCycleCallback(this);
    }

    private void onActivityTimeBegin(HashMap<Integer, Long> map,
                                     Activity activity) {
        if (activity == null)
            return;

        map.put(activity.hashCode(), System.currentTimeMillis());
    }

    private void onActivityTimeEnd(HashMap<Integer, Long> map,
                                   Activity activity, String log) {
        if (activity == null)
            return;

        int hashcode = activity.hashCode();
        Long begin = map.get(hashcode);
        if (begin != null) {
            long timeCost = System.currentTimeMillis() - begin;
            if (timeCost > TIME_COST_WARNING) {
                Toast.makeText(App.getContext(), "activity:" + activity.getComponentName().getShortClassName() + " " + log + " used " + timeCost + "ms", Toast.LENGTH_SHORT).show();
            }

            Log.i(TAG, log + "  activity: " + hashcode + " classname:"
                    + activity.getComponentName().getShortClassName()
                    + " use time: " + (System.currentTimeMillis() - begin));
        }
    }

    @Override
    public void onBeforeActivityCreate(Activity activity,
                                       Bundle savedInstanceState) {
        super.onBeforeActivityCreate(activity, savedInstanceState);
        onActivityTimeBegin(mCreateTimeUsed, activity);
    }

    @Override
    public void onAfterActivityCreate(Activity activity,
                                      Bundle savedInstanceState) {
        super.onAfterActivityCreate(activity, savedInstanceState);
        onActivityTimeEnd(mCreateTimeUsed, activity, "onActivityCreate");
    }

    @Override
    public void onBeforeActivityDestroy(Activity activity) {
        super.onBeforeActivityDestroy(activity);
        onActivityTimeBegin(mDestroyTimeUsed, activity);
    }

    @Override
    public void onAfterActivityDestroy(Activity activity) {
        super.onAfterActivityDestroy(activity);
        onActivityTimeEnd(mDestroyTimeUsed, activity, "onActivityDestroy");
    }

    @Override
    public void onBeforeActivityNewIntent(Activity activity, Intent intent) {
        super.onBeforeActivityNewIntent(activity, intent);
        onActivityTimeBegin(mNewIntentTimeUsed, activity);
    }

    @Override
    public void onAfterActivityNewIntent(Activity activity, Intent intent) {
        super.onAfterActivityNewIntent(activity, intent);
        onActivityTimeEnd(mNewIntentTimeUsed, activity, "onActivityNewIntent");
    }

    @Override
    public void onBeforeActivityPause(Activity activity) {
        super.onBeforeActivityPause(activity);
        onActivityTimeBegin(mPauseTimeUsed, activity);
    }

    @Override
    public void onAfterActivityPause(Activity activity) {
        super.onAfterActivityPause(activity);
        onActivityTimeEnd(mPauseTimeUsed, activity, "onActivityPause");
    }

    @Override
    public void onBeforeActivityResume(Activity activity) {
        super.onBeforeActivityResume(activity);
        onActivityTimeBegin(mResumeTimeUsed, activity);
    }

    @Override
    public void onAfterActivityResume(Activity activity) {
        super.onAfterActivityResume(activity);
        onActivityTimeEnd(mResumeTimeUsed, activity, "onActivityResume");
    }

    @Override
    public void onBeforeActivityStart(Activity activity) {
        super.onBeforeActivityStart(activity);
        onActivityTimeBegin(mStartTimeUsed, activity);
    }

    @Override
    public void onAfterActivityStart(Activity activity) {
        super.onAfterActivityStart(activity);
        onActivityTimeEnd(mStartTimeUsed, activity, "onActivityStart");
    }

    @Override
    public void onBeforeActivityStop(Activity activity) {
        super.onBeforeActivityStop(activity);
        onActivityTimeBegin(mStopTimeUsed, activity);
    }

    @Override
    public void onAfterActivityStop(Activity activity) {
        super.onAfterActivityStop(activity);
        onActivityTimeEnd(mStopTimeUsed, activity, "onActivityStop");
    }
}
