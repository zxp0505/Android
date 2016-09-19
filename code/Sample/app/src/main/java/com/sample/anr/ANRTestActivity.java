package com.sample.anr;

import android.app.Activity;
import android.os.Bundle;

public class ANRTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_test);
    }
}
