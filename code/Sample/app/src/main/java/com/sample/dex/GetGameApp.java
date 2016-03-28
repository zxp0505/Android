package com.sample.dex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by clarkehe on 3/1/16.
 * 根据包的特征码分析APP是不是游戏。
 */
public class GetGameApp {

    private final static String TAG = "GetGameList";

    private final static String[] GAME_PACKAGE_NAMES = {
            "com.unity3d.player",
            "org.cocos2dx.lib",
            "com.badlogic.gdx",
            "com.tencent.game.npengine",
            "com.tencent.game.helper",
            "cn.egame.terminal.sdk",
            "com.yinhan.lib.Cocos2dxHelper"
    };

    //"com.tencent.tmassistantsdk"//腾讯视频 花样直播
    //节奏大师、时空猎人
    //机战王、我的世界
    //新部落守卫战 ??
    private final static String[] GAME_CLASS_NAMES = {
            "com.unity3d.player.UnityPlayerNativeActivity",
            "org.cocos2dx.lib.Cocos2dxActivity",
            "cn.egame.terminal.sdk.EgameCoreActivity",
            "com.yinhan.lib.Cocos2dxHelper"
    };

    private GetGameApp() {
    }

    /**
     * 本机已安装的非系统app. 会比较耗时，不能在主线程中调用
     * @param context context
     * @return app列表
     */
    static public List<AppInfo> getInstalledApp(Context context) {

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        List<AppInfo> appList = new ArrayList<AppInfo>();

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);

            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());

            tmpInfo.print();

            //Only display the non-system app info
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.add(tmpInfo);
            }
        }

        return appList;
    }

    /**
     * 综合利用三种方法来判断app是不是游戏. 整个过程也会相对耗时，不能在主线程中调用。
     * @param context context
     * @param packageName app包名
     * @return true, false
     */
    static public boolean checkIsGame(Context context, String packageName)
    {
        Log.d(TAG, "checkIsGame, packageName:" + packageName);

        boolean bIsGame;
        do {

            bIsGame = checkIsGameFromDex(context, packageName);
            if (bIsGame) {
                break;
            }

            bIsGame = checkIsGameByLibName(context, packageName);
            if (bIsGame) {
                break;
            }

            bIsGame = checkIsGameFromMultiDex(context, packageName);
            if (bIsGame) {
                break;
            }

        }while (false);

        return bIsGame;
    }

    /**
     * 根据app中classes.dex文件是否包含特定游戏引擎或SDK的包名
     * @param context context
     * @param packageName app包名
     * @return true, false
     */
    static public boolean checkIsGameFromDex(Context context, String packageName) {
        Log.d(TAG, "checkIsGame, packageName:" + packageName);

        final String path = getApkPathByPackageName(context, packageName);
        if (path == null) {
            Log.d(TAG, "checkIsGame#getApkPathByPackageName failed.");
            return false;
        }

        DexFile dexFile = null;
        try {
            // get dex file of APK
            dexFile = new DexFile(path);
            Log.i(TAG, "checkIsGame, dex file name:" + dexFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (dexFile == null){
            return false;
        }

        return findInDexFile(dexFile);
    }

    static boolean findInDexFile(DexFile dexFile){

        Log.i(TAG, "findInDexFile, dex file name:" + dexFile.getName());

        boolean bGame = false;
        try {
            // travel all classes
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                final String className = entries.nextElement();
                //Log.d(TAG, "className: " + className);
                if (isContainGamePackageName(className)) {
                    Log.i(TAG, "checkIsGame, found game package.");
                    bGame = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bGame;
    }

    static private boolean isContainGamePackageName(final String className) {
        final int size = GAME_PACKAGE_NAMES.length;
        for (int i = 0; i < size; i++) {
            if (className.startsWith(GAME_PACKAGE_NAMES[i])) {
                Log.i(TAG, "find game package: " + GAME_PACKAGE_NAMES[i]);
                return true;
            }
        }
        return false;
    }

    /**
     * app可能会分包，从classes2.dex classes3.dex ...判断是否有游戏引擎或SDK的类名
     * @param context context
     * @param packageName app包名
     * @return true false
     */
    static public boolean checkIsGameFromMultiDex(Context context, String packageName) {
        Log.d(TAG, "checkIsGameFromMultiDex, packageName:" + packageName);

        List<File> files = extractMultiDexFilesFromApp(context, packageName);
        if (files == null || files.size() == 0) return false;

        final int size = files.size();
        Log.d(TAG, "checkIsGameFromMultiDex, dex file count: " + size);

        for (int i = 0; i < size; i++) {
            boolean ret = checkDexHasGameClassName(context, files.get(i).getAbsolutePath());
            if (ret) return true;
        }
        return false;
    }

    static private List<File> extractMultiDexFilesFromApp(Context context, String packageName) {
        Log.d(TAG, "extractMultiDexFilesFromApp, packageName:" + packageName);

        final String path = getApkPathByPackageName(context, packageName);
        if (path == null) {
            Log.e(TAG, "extractMultiDexFilesFromApp#getApkPathByPackageName failed.");
            return null;
        }

        List<File> files = null;
        File dexDir = new File(context.getCacheDir(), "-secondary-dexes");
        try {
            files = MultiDexExtractor.load(context, path, dexDir, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    static private boolean checkDexHasGameClassName(Context context, String dexFilePath) {
        Log.d(TAG, "checkDexHasGameClassName, dexFile: " + dexFilePath);

        //String cachePath = Environment.getExternalStorageDirectory() + "/";
        File dexOutputDir = new File(context.getCacheDir(), "-secondary-dexes-out");
        dexOutputDir.mkdir();

        DexFile dexFile = null;
        try {
            File file = new File(dexFilePath);
            dexFile = DexFile.loadDex(dexFilePath, dexOutputDir.getAbsolutePath() + "/" + file.getName(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (dexFile == null) {
            return false;
        }

        return findInDexFile(dexFile);

/**
        DexClassLoader classLoader = new DexClassLoader(dexFile, dexOutputDir.getAbsolutePath(),
                null, context.getClassLoader());

        final int size = GAME_CLASS_NAMES.length;
        for (int i = 0; i < size; i++) {
            Class loadClass = null;
            try {
                loadClass = classLoader.loadClass(GAME_CLASS_NAMES[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (loadClass != null) {
                Log.i(TAG, "checkDexHasGameClassName, find class: " + GAME_CLASS_NAMES[i]);
                return true;
            }
        }
        return false;
 */

    }



    static private String getApkPathByPackageName(Context context, String packageName) {
        Log.d(TAG, "getApkPathByPackageName," + "packageName :" + packageName);

        String path = null;
        try {
            path = context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "getApkPathByPackageName," + "path :" + path);
        return path;
    }

    /**
     * 根据app的包中是否包含游戏引擎的lib so文件来判断。
     * @param context context
     * @param packageName app包名
     * @return true false
     */
    static public boolean checkIsGameByLibName(Context context, String packageName)
    {
        Log.d(TAG, "checkIsGameByLibName, packageName:" + packageName);

        final String path = getApkPathByPackageName(context, packageName);
        if (path == null) {
            Log.e(TAG, "checkIsGameByLibName#getApkPathByPackageName failed.");
            return false;
        }

        boolean bHasGameLib = false;

        final File sourceApk = new File(path);
        ZipFile apk = null;
        try{
            apk = new ZipFile(sourceApk);
            Enumeration<? extends ZipEntry> entries = apk.entries();
            while (entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                final String name = entry.getName();
                if (name.startsWith("lib")) {
                    Log.d(TAG, "Entry name: " + name);
                    if (isNameContainGameLibName(name)) {
                        bHasGameLib = true;
                        break;
                    }
                }
            }
        }catch (IOException e){
            Log.e(TAG, "Failed to open resource", e);
        }
        finally {
            if (apk != null) {
                try {
                    apk.close();
                } catch (IOException e) {
                    Log.w(TAG, "Failed to close resource", e);
                }
            }
        }

        return bHasGameLib;
    }

    //可用正则表达式
    //lib/armeabi-v7a/libunity.so
    //lib/armeabi-v7a/libcocos2dcpp.so
    //lib/armeabi/libcocos2dlua.so
    //lib/armeabi/libcocos2dlua.so
    //lib/armeabi/libkamcord.so?? https://github.com/kamcord/kamcord-android-sdk
    //http://www.fmod.org/fmod-ex/
    private final static String[] GAME_LIB_NAMES = {
            "libunity.so",
            "libcocos2dcpp.so",
            "libcocos2dlua.so",
            "libtersafe.so",
    };

    static private boolean isNameContainGameLibName(String fileName) {
        return fileName.contains(GAME_LIB_NAMES[0])
                || fileName.contains(GAME_LIB_NAMES[1])
                || fileName.contains(GAME_LIB_NAMES[2])
                || fileName.contains(GAME_LIB_NAMES[3]);
    }

    public static class AppInfo {
        public String appName = "";
        public String packageName = "";
        public String versionName = "";
        public int versionCode = 0;
        public Drawable appIcon = null;

        public void print() {
            Log.v(TAG, "Name:" + appName + " Package:" + packageName);
            Log.v(TAG, "Name:" + appName + " versionName:" + versionName);
            Log.v(TAG, "Name:" + appName + " versionCode:" + versionCode);
        }
    }

    /**
    cn.egame.terminal.sdk.EgameCoreActivity
    com.unity3d.player.UnityPlayerNativeActivity

    com.yinhan.lib.Cocos2dxHelper
    com.yinhan.lib.Natives
    com.yinhan.lib.GameRender

    com.tencent.game.helper.CustomPushReceiver
    com.tencent.game.helper.TextInputWraper

    03-02 11:22:31.016 18494-18494/com.sample E/GetGameList: find game package: org.cocos2dx.lib
    03-02 11:22:31.016 18494-18494/com.sample E/GetGameList: checkIsGame, found game package.
            03-02 11:22:31.016 18494-18494/com.sample D/GetGameList: checkIsGameByLibName, packageName:com.qqgame.hlddz
    03-02 11:22:31.016 18494-18494/com.sample E/GetGameList: getApkPathByPackageName,packageName :com.qqgame.hlddz
    03-02 11:22:31.016 18494-18494/com.sample E/GetGameList: getApkPathByPackageName,path :/data/app/com.qqgame.hlddz-1.apk
    03-02 11:22:31.096 18494-18494/com.sample D/dalvikvm: GC_FOR_ALLOC freed 6737K, 19% free 29732K/36484K, paused 24ms, total 24ms
    03-02 11:22:31.136 18494-18494/com.sample E/GetGameList: Entry name: lib/armeabi/libtpnsSecurity.so
    03-02 11:22:31.136 18494-18494/com.sample E/GetGameList: Entry name: lib/armeabi/libtersafe.so
    03-02 11:22:31.136 18494-18494/com.sample E/GetGameList: Entry name: lib/armeabi/libtpnsWatchdog.so
    03-02 11:22:31.136 18494-18494/com.sample E/GetGameList: Entry name: lib/armeabi/libBugly.so
    03-02 11:22:31.136 18494-18494/com.sample E/GetGameList: Entry name: lib/armeabi/libcftutils.so
    03-02 11:22:31.136 18494-18494/com.sample E/GetGameList: Entry name: lib/armeabi/libwtecdh.so
    03-02 11:22:31.136 18494-18494/com.sample E/GetGameList: Entry name: lib/armeabi/libNewHLDDZ.so
    */

     }
