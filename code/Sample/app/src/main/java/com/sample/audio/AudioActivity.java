package com.sample.audio;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.sample.R;

public class AudioActivity extends Activity {

    PlaySound playSound;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        findViewById(R.id.frontBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(0, 0);
            }
        });

        findViewById(R.id.rightBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(90, 0);
            }
        });

        findViewById(R.id.backBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(0, 180);
            }
        });
    }

    float x, y, z;
    float t = 0;

    private void play(final float azimuth, final float elevation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                playSound = new PlaySound(AudioActivity.this);
                playSound.setPos(azimuth, elevation);
                playSound.play();
            }
        }).start();

        postDelay();
    }

    void postDelay() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setPost();
            }
        }, 50);
    }

    void setPost() {
        x = (float) Math.sin(t);
        y = (float) Math.cos(t);
        z = 0;
        t += 0.05;

        float[] ret = IR.cartesianToInteraural(x, y, z);
        if (playSound != null) {
            playSound.setPos(ret[1], ret[2]);
        }

        postDelay();
    }
}
