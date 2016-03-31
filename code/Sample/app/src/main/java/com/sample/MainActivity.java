package com.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sample.classLoader.ClassLoaderActivity;
import com.sample.classLoader.getgameapp.GameAppListActivity;
import com.sample.lifecycle.ActivityA;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ListView mListView;
    private ItemInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.setData(initItemInfo());
                mAdapter.notifyDataSetChanged();
            }
        }, 100);
    }

    private void bindView(){
        mListView = (ListView)findViewById(R.id.listview);
        mAdapter = new ItemInfoAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    private ArrayList<ItemInfo> initItemInfo() {
        ArrayList<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
        itemInfoList.add(new ItemInfo("Activity LifeCycle", ActivityA.class));
        itemInfoList.add(new ItemInfo("ClassLoader", ClassLoaderActivity.class));
        itemInfoList.add(new ItemInfo("Game App List", GameAppListActivity.class));
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
}
