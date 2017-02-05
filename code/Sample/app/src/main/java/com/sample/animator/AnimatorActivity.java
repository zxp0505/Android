package com.sample.animator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.sample.R;

public class AnimatorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animator);
        findViewById(R.id.startAnimator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    void start() {
        TranslateAnimation animation = new TranslateAnimation(500, 0, 500, 0);
        animation.setDuration(3000);
        animation.setInterpolator(new OvershootInterpolator());

        View view = findViewById(R.id.startAnimator);
        view.startAnimation(animation);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }
    }
}
