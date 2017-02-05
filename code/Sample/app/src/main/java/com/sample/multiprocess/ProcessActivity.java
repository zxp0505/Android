package com.sample.multiprocess;

import com.sample.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class ProcessActivity extends Activity {

    private static String TAG = "ProcessActivity";

    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        findViewById(R.id.butStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binderToService();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void binderToService() {
        Intent intent = new Intent(ProcessActivity.this.getApplicationContext(), ProcessService.class);
        //startService(intent);
        boolean ret = this.getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "binderToService,ret:" + ret);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            //LocalBinder binder = (LocalBinder) service;
            //mService = binder.getService();
            Log.d(TAG, "onServiceConnected");
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected");
            mBound = false;
        }
    };
}
