package com.sample.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sample.R;

// 源图片大小，
// TARGET大小，确定TARGET大小
// 缩放

// ImageView又有转换（fitcenter, centercrop）
// DECODE

public class GlideActivity extends Activity {

    private final static String TAG = "GlideActivity|Glide";

    //980x500
    final String url = "http://ossweb-img.qq.com/images/lol/web201310/skin/big222000.jpg";

    //100x100
    final String url2 = "http://q3.qlogo.cn/g?b=qq&k=tysUcdic0LwrPOIK3PeWic3w&s=100&t=1457753232";

    final String url3 = "http://p.qpic.cn/qqtalk_snapshot/2123176/1468914847796807/320";

    final String url4 = "http://p.qpic.cn/qtlol/0/b9aa30750469afc192102c090effc820T1468585088986699/420";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        bindView();
    }

    private void bindView() {
        float dimen_10 = getResources().getDimension(R.dimen.dimen_10);
        Log.d(TAG, "dimen_10:" + dimen_10);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).load(url).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);

        findViewById(R.id.but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
                Glide.with(GlideActivity.this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView2);
            }
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Drawable drawable =  imageView.getDrawable();
//                if (drawable != null) {
//                    final int width1 = drawable.getIntrinsicWidth();
//                    final int height1 = drawable.getIntrinsicHeight();
//                    Log.d(TAG, "width1:" + width1);
//                    Log.d(TAG, "height1:" + height1);
//                }
//            }
//        }, 3000);

        //loadImage(this, url, null, -1, -1);
    }

    public static void loadImage(Context context, String url, final TextView tv, final int w, final int h) {
        try {
            Glide.with(context).load(url).asBitmap().dontAnimate().override(100, 200)
                    .placeholder(R.drawable.ic_launcher).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                    final int width = resource.getWidth();
                    final int height = resource.getHeight();
                    Log.d(TAG, "width:" + width);
                    Log.d(TAG, "height:" + height);

                    if (w > 0 && h > 0) {

                        //  resource = scaleBitmap(resource, w, h);//Glide的override方法仅适用于ImageView
                    }
                    // TabWidgetHelper.setViewIcon(ApplicationContextHolder.getAppContext(), tv, resource, TabWidgetHelper.DrawableOrientation.TOP, false);
                }
            });
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
