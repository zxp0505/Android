package com.sample.record;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.FullFrameRect;
import com.android.grafika.gles.OffscreenSurface;
import com.android.grafika.gles.Texture2dProgram;
import com.sample.R;
import com.tencent.qqgamemi.srp.media.newapi.ScreenRecorderManager;

public class RecordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        findViewById(R.id.butStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                test();

//                boolean ret = ScreenCapture.requestPermission(RecordActivity.this, new Runnable() {
//                    @Override
//                    public void run() {
//                        ScreenRecorderManager.getInstance().startRecord(RecordActivity.this,
//                                ScreenCapture.gerResultCode(), ScreenCapture.getData());
//                    }
//                });
//
//                if (ret){
//                    ScreenRecorderManager.getInstance().startRecord(RecordActivity.this,
//                            ScreenCapture.gerResultCode(), ScreenCapture.getData());
//                }
            }
        });

        findViewById(R.id.butStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScreenRecorderManager.getInstance().stopRecord(RecordActivity.this);
            }
        });
    }

    private void test() {
        final int width = 100;
        final int height = 100;

        EglCore mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);

        //mOffscreenSurface在此处作用？？？
        OffscreenSurface mOffscreenSurface = new OffscreenSurface(mEglCore, width, height);
        mOffscreenSurface.makeCurrent();

        FullFrameRect mFullFrameBlit = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        int mTextureId = mFullFrameBlit.createTextureObject();
    }
}
