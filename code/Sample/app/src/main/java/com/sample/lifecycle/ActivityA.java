package com.sample.lifecycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityA extends Activity{

    public ActivityA() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("Start Activity B");
        this.setContentView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityA.this, ActivityB.class);
                ActivityA.this.startActivity(intent);
            }
        });
    }

    static {
        RegActivityLifecycleCallback.regLifeCycleCallBack();
    }
}
