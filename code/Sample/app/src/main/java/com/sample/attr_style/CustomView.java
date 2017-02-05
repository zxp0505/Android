package com.sample.attr_style;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sample.R;

/**
 * Created by clarkehe on 13/7/16.
 */
public class CustomView extends View {

    private final static String TAG = "CustomView";

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.theme_style);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.custom_attrs, defStyleAttr, R.style.default_style);

        String one = a.getString(R.styleable.custom_attrs_custom_attr1);
        String two = a.getString(R.styleable.custom_attrs_custom_attr2);

        Log.d(TAG, "one:" + one);
        Log.d(TAG, "two:" + two);

        a.recycle();
    }
}

/**
 * 有那些属性：
 * <p>
 * 属性值的来源：
 * 1. AttributeSet, 来自XML指定属性及style
 * <p>
 * set , 0 , 0    : xm1  xml2
 * set,  x , 0    : set 没有 theme1,  xml2
 * set,  x, x
 */
