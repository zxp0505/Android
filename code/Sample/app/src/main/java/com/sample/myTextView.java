package com.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


/**
 * Created by clarkehe on 1/15/16.
 * Todo:
 */
public class myTextView extends TextView {

    private static final String TAG = "myTextView";

    public myTextView(Context context) {
        super(context);
    }

    public myTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint.FontMetrics fontMetrics = this.getPaint().getFontMetrics();
        Log.d(TAG, "ascent: " + fontMetrics.ascent);
        Log.d(TAG, "descent: " + fontMetrics.descent);
        Log.d(TAG, "top: " + fontMetrics.top);
        Log.d(TAG, "bottom: " + fontMetrics.bottom);
        Log.d(TAG, "leading: " + fontMetrics.leading);


        String str = this.getText().toString();
        //str = "一";
        Rect rect = new Rect();
        this.getPaint().getTextBounds(str, 0, str.length(), rect);
        Log.d(TAG, rect.toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        Log.d(TAG, "width: " + width);
        Log.d(TAG, "height: " + height);
    }
}
