package com.sample.crashreport;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.sample.R;

public class TestCrashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_crash);

        findViewById(R.id.catchCrashBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatchCrash.catchUnhandledException();
            }
        });

        findViewById(R.id.makeCrashBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String test = null;
                        Log.d("test", "" + test.length());
                    }
                });

                thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        Log.d("TEST", "private: uncaughtException");
                    }
                });

                thread.start();
            }
        });
    }
}
