package com.sample.TEST;

import android.content.Context;
import android.widget.ImageView;

import com.sample.R;

/**
 * Created by clarkehe on 1/14/16.
 * Todo:  TestView
 */
public class TestView {

    public TestView(Context context)
    {
        ImageView image = new ImageView(context);
        image.setBackgroundResource(R.mipmap.startup_twinkle_animation4);
    }
}

