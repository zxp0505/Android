package com.sample.sleep;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sample.R;

import java.util.Calendar;

public class TestSleepActivity extends Activity {

    private static String TAG = "TestSleepActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sleep);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //postDelayedMsg();

        setAlarm(this);

        //   createThread();

//        createThread1();

        //playVideo();
    }

    private void playVideo() {
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/test.mp4");
        Log.d(TAG, uri.toString());
        VideoView videoView = (VideoView) this.findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();
    }

    private void createThread1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (Exception e) {

                    }
                    Log.d(TAG, "Delayed Msg.");
                }
            }
        }).start();
    }

    private void createThread() {
        HandlerThread thread = new HandlerThread("test");
        thread.start();

        Handler handler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        postDelayedMsg(handler);
    }

    private void postDelayedMsg(final Handler handler) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Delayed Msg.");
                postDelayedMsg(handler);
            }
        }, 10 * 1000);
    }

    private void postDelayedMsg() {
        Handler mHandler = new Handler(Looper.getMainLooper());

        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Delayed Msg.");
            }
        };

        long delay = 5 * 60 * 1000;
        mHandler.postDelayed(mRunnable, delay);
    }

    static void setAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Intent intent = new Intent(context, SimpleBroadCastReceiver.class);
        Intent intent = new Intent("com.sample.alarm");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long delay = 10 * 1000;
        alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay, alarmIntent);
    }

    public static class SimpleBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SimpleBroadCastReceiver#onReceive");
            //setAlarm(SimpleBroadCastReceiver.this);
        }
    }
}
