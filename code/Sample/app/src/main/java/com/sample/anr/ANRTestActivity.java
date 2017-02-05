package com.sample.anr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sample.R;

public class ANRTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_test);

        findViewById(R.id.but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(6000);
                } catch (Exception e) {

                }
            }
        });
    }
}
