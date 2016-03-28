package com.sample.TEST;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.sample.R;

/**
 * Created by clarkehe on 1/14/16.
 * Todo:
 */
public class NewTestView {

    public NewTestView(Context context)
    {
        ImageView image = new ImageView(context);
        image.setBackgroundResource(R.mipmap.startup_twinkle_animation4);

        TestView view = new TestView(context);
        Log.d("TAG", view.toString());
    }
}
