package com.sample.classLoader.getgameapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sample.R;

import java.io.File;
import java.util.List;

import dalvik.system.DexClassLoader;

public class GameAppListActivity extends Activity {

    private final static String TAG = "ListViewActivity";

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
                appList = GetGameApp.getInstalledApp(GameAppListActivity.this);
                mAdapter.setData(appList);
                mAdapter.notifyDataSetChanged();
            }
        }, 100);
    }

    List<GetGameApp.AppInfo> appList;

    public class CodeLearnAdapter extends BaseAdapter
    {
        private final Activity context;

        List<GetGameApp.AppInfo> mAppList;

         class ViewHolder {
            public TextView text;
            public ImageView image;
        }

        public CodeLearnAdapter(Activity context) {
            this.context = context;
        }

        public void setData(List<GetGameApp.AppInfo> _appList)
        {
            mAppList = _appList;
        }

        @Override
        public int getCount() {
            if (mAppList == null) return 0;
            return mAppList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return "" + arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

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

            final GetGameApp.AppInfo appInfo = mAppList.get(position);
            final String packageName = mAppList.get(position).packageName;

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            appInfo.bGame = GetGameApp.checkIsGame(GameAppListActivity.this, packageName);
                            if (appInfo.bGame){
                                GameAppListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }.start();
                }
            });

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
}

//0. 特征码. 规则添加？ 混淆。分DEX.
//1. 性能，还OK
//2. APK可读, 判断SO文件？
