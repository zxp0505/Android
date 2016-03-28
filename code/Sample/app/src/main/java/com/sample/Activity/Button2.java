package com.sample.Activity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by clarkehe on 3/15/16.
 * Todo:
 */
public class Button2 extends Button {

    final String TAG = "Button2";

    public Button2(Context context) {
        super(context);
    }

    public Button2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Button2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        Log.d(TAG, "onTouchEvent, ret:" + ret);
        return ret;
    }
}
