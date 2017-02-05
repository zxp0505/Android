package com.sample;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sample.animator.AnimatorActivity;
import com.sample.anr.ANRTestActivity;
import com.sample.architect.mvc.MVCActivity;
import com.sample.attr_style.AttrStyleActivity;
import com.sample.audio.AudioActivity;
import com.sample.audio.PlaySound;
import com.sample.classLoader.ClassLoaderActivity;
import com.sample.classLoader.getgameapp.GameAppListActivity;
import com.sample.crashreport.TestCrashActivity;
import com.sample.dialog.DialogActivity;
import com.sample.glide.GlideActivity;
import com.sample.hotfix.dex.HotFixActivity;
import com.sample.lifecycle.ActivityA;
import com.sample.multiprocess.ProcessActivity;
import com.sample.network.NetworkActivity;
import com.sample.opengles.OpenGLActivity;
import com.sample.record.RecordActivity;
import com.sample.sleep.TestSleepActivity;
import com.sample.statsUsage.StatsUsageActivity;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ListView mListView;
    private ItemInfoAdapter mAdapter;

    ArrayList<ItemInfo> mDatas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();

        int maxMemory = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int largeMaxMemory = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getLargeMemoryClass();
        Log.d("MainActivity", "maxMemory:" + maxMemory);
        Log.d("MainActivity", "largeMaxMemory:" + largeMaxMemory);

        mDatas = initItemInfo();
        mAdapter.setData(mDatas);

//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 100);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //mAdapter.notifyDataSetChanged();
//                for (int i= 0; i < 1000; i++){
////                    try{
////                        Thread.sleep(500);
////                    }catch (Exception e){}
//
//                    mDatas.add(0, mDatas.get(0));
//
//                    notify11();
//                }
//            }
//        }).start();

//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        for (Network network : connectivityManager.getAllNetworks()) {
//            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
//            if (networkInfo.isConnected()) {
//                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
//                Log.d("DnsInfo", "iface = " + linkProperties.getInterfaceName());
//                Log.d("DnsInfo", "dns = " + linkProperties.getDnsServers());
//                //return linkProperties.getDnsServers();
//            }
//        }
    }

    private void bindView(){
        mListView = (ListView)findViewById(R.id.listview);
        mAdapter = new ItemInfoAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    //    void notify11(){
//        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                mAdapter.notifyDataSetChanged();
//            }
//        });
//    }

    private ArrayList<ItemInfo> initItemInfo() {
        ArrayList<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
        itemInfoList.add(new ItemInfo("AudioActivity", AudioActivity.class));
        itemInfoList.add(new ItemInfo("Activity LifeCycle", ActivityA.class));
        itemInfoList.add(new ItemInfo("ClassLoader", ClassLoaderActivity.class));
        itemInfoList.add(new ItemInfo("Game App List", GameAppListActivity.class));
        itemInfoList.add(new ItemInfo("Record Screen", RecordActivity.class));
        itemInfoList.add(new ItemInfo("OpenGL", OpenGLActivity.class));
        itemInfoList.add(new ItemInfo("ProcessActivity", ProcessActivity.class));
        itemInfoList.add(new ItemInfo("AttrStyleActivity", AttrStyleActivity.class));
        itemInfoList.add(new ItemInfo("GlideActivity", GlideActivity.class));
        itemInfoList.add(new ItemInfo("StatsUsageActivity", StatsUsageActivity.class));
        itemInfoList.add(new ItemInfo("DialogActivity", DialogActivity.class));
        itemInfoList.add(new ItemInfo("HotFixActivity", HotFixActivity.class));
        itemInfoList.add(new ItemInfo("TestCrashActivity", TestCrashActivity.class));
        itemInfoList.add(new ItemInfo("TestSleepActivity", TestSleepActivity.class));
        itemInfoList.add(new ItemInfo("ANRTestActivity", ANRTestActivity.class));
        itemInfoList.add(new ItemInfo("NetworkActivity", NetworkActivity.class));
        itemInfoList.add(new ItemInfo("MVCActivity", MVCActivity.class));
        itemInfoList.add(new ItemInfo("AnimatorActivity", AnimatorActivity.class));

        return itemInfoList;
    }

    private static class ItemInfo {
        public ItemInfo(String _text, Class<?> _clazz) {
            text = _text;
            clazz = _clazz;
        }
        String text;
        Class<?> clazz;
    }

    private static class ItemInfoAdapter extends BaseAdapter {

        private final Activity context;
        ArrayList<ItemInfo> mAppList;

        class ViewHolder {
            public TextView text;
        }

        public ItemInfoAdapter(Activity context) {
            this.context = context;
        }

        public void setData(ArrayList<ItemInfo> _appList) {
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
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.rowlayout, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.label);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            String s = mAppList.get(position).text;
            holder.text.setText(s);

            final ItemInfo itemInfo = mAppList.get(position);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, itemInfo.clazz);
                    context.startActivity(intent);
                }
            });
            return rowView;
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

}
