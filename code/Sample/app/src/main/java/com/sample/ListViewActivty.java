package com.sample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sample.dex.GetGameApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;


public class ListViewActivty extends Activity {

    private final static String TAG = "ListViewActivty";

    ListView mListView;
    CodeLearnAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_activty);
        mListView = (ListView)findViewById(R.id.listview);

        mAdapter = new CodeLearnAdapter(this);
        mListView.setAdapter(mAdapter);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getInstalledApp();
                mAdapter.setData(appList);
                mAdapter.notifyDataSetChanged();
            }
        }, 100);
    }

    public class AppInfo {
        public String appName="";
        public String packageName="";
        public String versionName="";
        public int versionCode=0;
        public Drawable appIcon=null;
        public Boolean bGame = null;
        public void print()
        {
            Log.v("app", "Name:" + appName + " Package:" + packageName);
            Log.v("app","Name:"+appName+" versionName:"+versionName);
            Log.v("app","Name:"+appName+" versionCode:"+versionCode);
        }
    }

    ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据

    private void getInstalledApp() {

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());

            tmpInfo.print();

            //Only display the non-system app info
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.add(tmpInfo);//如果非系统应用，则添加至appList
            }
        }
    }

    public class CodeLearnAdapter extends BaseAdapter
    {
        private final Activity context;

        ArrayList<AppInfo> mAppList;

         class ViewHolder {
            public TextView text;
            public ImageView image;
        }

        public CodeLearnAdapter(Activity context) {
            this.context = context;
        }

        public void setData(ArrayList<AppInfo> _appList)
        {
            mAppList = _appList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (mAppList == null) return 0;

            return mAppList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return new String("" + arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            /**
            System.out.println("Stack***********************" + position);
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                System.out.println(ste);
            }*/

            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.rowlayout, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.label);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            String s = mAppList.get(position).appName;
            holder.text.setText(s);

            final AppInfo appInfo = mAppList.get(position);
            final String packageName = mAppList.get(position).packageName;

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //enumClass(ListViewActivty.this, packageName, true);

                    appInfo.bGame = GetGameApp.checkIsGame(ListViewActivty.this, packageName);

                    if (appInfo.bGame){
                        notifyDataSetChanged();
                    }

                    /**
                    try {
                        MultiDexHelper.getAllClasses(ListViewActivty.this);
                    }
                    catch (Exception e){

                    }*/
                }
            });

            /**

            if (appInfo.bGame == null){
                appInfo.bGame = enumClass(ListViewActivty.this, packageName, false);
            }
*/
            if (appInfo.bGame == null || !appInfo.bGame){
                rowView.setBackgroundColor(Color.BLACK);
            }else{
                rowView.setBackgroundColor(Color.RED);
            }
            return rowView;
        }
    }

    private boolean enumClass(Context context, String packageName, boolean printLog) {
        String path = null;

        long start = System.currentTimeMillis();
        Log.d(TAG, "Start: " + start);

        try {
            path = context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
            // 获得某个程序的APK路径
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //path += ":classes.dex";

        //path = "/data/local/tmp/classes2_happy.zip";

        //path = "/data/local/tmp/com.tencent.gamejoy-2.apk.classes2.zip";

       // path = "/data/data/com.tencent.gamejoy/code_cache/secondary-dexes/com.tencent.gamejoy-2.apk.classes2.zip";

        //com.unity3d.player
        //org.cocos2dx.lib
        //com.badlogic.gdx
        //com.tencent.game.npengine
        //com.tencent.tmassistantsdk
        //cn.egame.terminal.sdk

        Log.e(TAG, packageName);
        Log.e(TAG, path);

        if (path == null) return false;


        Class mLoadClass = null;
        //String cachePath = Environment.getExternalStorageDirectory() + "/";
        File dexOutputDir = context.getCacheDir();
        DexClassLoader classLoader = new DexClassLoader(path, dexOutputDir.getAbsolutePath(), null, getClassLoader());
        try{
            //
            mLoadClass = classLoader.loadClass("android.support.v4.content.ContextCompat");
            //mLoadClass = classLoader.loadClass("org.cocos2dx.lib.Cocos2dxActivity");
        }catch (Exception e){

        }

        if (mLoadClass != null){
            Log.e(TAG, "find class");
            return true;
        }

        return false;
        /*

        boolean bGame = false;

        try {
            DexFile dexFile = new DexFile(path);// get dex file of APK

            Log.e(TAG, "dex file name:" + dexFile.getName());

            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) { // travel all classes
                String className = (String) entries.nextElement();
                if (printLog) {
                    Log.e(TAG, className);
                }
                //if (className.contains("com.unity3d.player")){
                if (isGame(className)){
                    Log.e(TAG, "Found it");
                    bGame = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        Log.d(TAG, "end: " + end);
        Log.d(TAG, "cost: " + (end - start));

        return bGame;
        */
    }

    private boolean isGame(final String className)
    {
        String [] strings = {"com.unity3d.player",
                "org.cocos2dx.lib",
                "com.badlogic.gdx",
                "com.tencent.game.npengine",
                "com.tencent.tmassistantsdk",
                "cn.egame.terminal.sdk"};
        int size = strings.length;
        for (int i=0; i<size; i++){
            if (className.contains(strings[i])){
                return true;
            }
        }
        return false;
    }

}

//0. 特征码. 规则添加？ 混淆。分DEX.
//1. 性能，还OK
//2. APK可读, 判断SO文件？
