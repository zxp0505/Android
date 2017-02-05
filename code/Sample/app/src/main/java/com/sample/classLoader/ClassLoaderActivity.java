package com.sample.classLoader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ClassLoaderActivity extends Activity {

    Handler mHandler = new Handler(Looper.getMainLooper());
    private final static String TAG = "classLoader";

    public ClassLoaderActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView view = new TextView(this);
        setContentView(view);

        String classLoaderStr = "Current Activity ClassLoader:";
        ClassLoader classLoader = getClassLoader();

        int i = 1;
        if (classLoader != null){
            Log.i(TAG, "[onCreate] classLoader " + i + " : " + classLoader.toString());

            classLoaderStr += "\r\n\r\n";
            classLoaderStr += (i + ". ");
            classLoaderStr += classLoader.toString();

            while (classLoader.getParent()!=null){
                classLoader = classLoader.getParent();

                i += 1;
                Log.i(TAG,"[onCreate] classLoader " + i + " : " + classLoader.toString());

                classLoaderStr += "\r\n\r\n";
                classLoaderStr += (i + ". ");
                classLoaderStr += classLoader.toString();
            }
        }
        view.setText(classLoaderStr);

//        final String curApp  = printForegroundTask(this);
//        Log.i(TAG, "curApp=" + curApp);
//
//        schedule();
    }


    void printClassLoader() {
        int i = 1;
        ClassLoader classLoader = getClassLoader();
        if (classLoader != null) {
            Log.i(TAG, "classLoader " + i + " : " + classLoader.toString());
            while (classLoader.getParent() != null) {
                classLoader = classLoader.getParent();
                i += 1;
                Log.i(TAG, "classLoader " + i + " : " + classLoader.toString());
            }
        }
    }

//    void schedule(){
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                test(ClassLoaderActivity.this);
//                schedule();
//            }
//        }, 1000);
//    }

//    public static String printForegroundTask(Context context) {
//        String currentApp = "Null";
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            UsageStatsManager usm = (UsageStatsManager) context
//                    .getSystemService("usagestats");
//            long time = System.currentTimeMillis();
//            List<UsageStats> appList = usm.queryUsageStats(
//                    UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
//            if (appList != null && appList.size() > 0) {
//                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
//                for (UsageStats usageStats : appList) {
//                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
//                }
//                if (mySortedMap != null && !mySortedMap.isEmpty()) {
//                    currentApp = mySortedMap.get(mySortedMap.lastKey())
//                            .getPackageName();
//                }
//            }
//        } else {
//            ActivityManager am = (ActivityManager) context
//                    .getSystemService(Context.ACTIVITY_SERVICE);
//            currentApp = am.getRunningTasks(1).get(0).topActivity
//                    .getPackageName();
//
//        }
//        return currentApp;
//    }


    void test(Context context) {

        Hashtable<String, List<ActivityManager.RunningServiceInfo>> hashtable = new Hashtable<String, List<ActivityManager.RunningServiceInfo>>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {
//            if (isCanceled()) {
//                return;
//            }

            String pkgName = rsi.service.getPackageName();

            if (rsi.foreground) {
                Log.d(TAG, "pkgName=" + pkgName);
                Log.d(TAG, "foreground=" + rsi.foreground);
                Log.d(TAG, "process=" + rsi.process);
                Log.d(TAG, "pid=" + rsi.pid);
            }

            if (hashtable.get(pkgName) == null) {
                List<ActivityManager.RunningServiceInfo> list = new ArrayList<ActivityManager.RunningServiceInfo>();
                list.add(rsi);
                hashtable.put(pkgName, list);
            } else {
                hashtable.get(pkgName).add(rsi);
            }
        }

//        int i = 0;
//        int size = hashtable.size();
//        for (Iterator it = hashtable.keySet().iterator(); it.hasNext(); i++) {
//            String key = (String) it.next();
//            List<ActivityManager.RunningServiceInfo> value = hashtable.get(key);
//            ProcessItem item = new ProcessItem(getContext(), value.get(0).pid, key, totalCpu, totalRam);
//            if (!whiteList.contains(item.pkgName)) {
//                if (!killList.contains(item.pkgName)) {
//                    killList.add(item.pkgName);
//                    ramTotal += item.ram;
//
//                    if (getListener() != null) {
//                        Progress progress = new Progress(this);
//                        progress.setArg1(i);
//                        progress.setArg2(size);
//                        progress.setMsg(item.appName);
//                        progress.setObj(item);
//                        getListener().onExamining(progress);
//                    }
//                }
//            }
//        }
        hashtable.clear();
        hashtable = null;
    }
}

/**
 *
  classLoader 1 : dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.sample-1/base.apk"],nativeLibraryDirectories=[/data/app/com.sample-1/lib/arm, /vendor/lib, /system/lib]]]
  classLoader 2 : java.lang.BootClassLoader@8090c88
 */
