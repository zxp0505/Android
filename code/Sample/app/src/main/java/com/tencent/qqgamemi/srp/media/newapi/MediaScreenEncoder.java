package com.tencent.qqgamemi.srp.media.newapi;
/*
 * ScreenRecordingSample
 * Sample project to cature and save audio from internal and video from screen as MPEG4 file.
 *
 * Copyright (c) 2015 saki t_saki@serenegiant.com
 *
 * File name: MediaScreenEncoder.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
*/

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.FullFrameRect;
import com.android.grafika.gles.OffscreenSurface;
import com.android.grafika.gles.Texture2dProgram;
import com.android.grafika.gles.WindowSurface;

import java.io.File;
import java.io.IOException;


@SuppressLint("NewApi")
public class MediaScreenEncoder extends MediaVideoEncoderBase {
    private static final boolean DEBUG = true;    // TODO set false on release
    private static final String TAG = "MediaScreenEncoder";

    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 15;

    private MediaProjection mMediaProjection;
    private final int mDensity;
    private Surface mSurface;

    private int mBitRate;

    public MediaScreenEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener,
                              final MediaProjection projection, final int width, final int height, final int density, final int bitRate) {
        super(muxer, listener, width, height);
        mMediaProjection = projection;
        mDensity = density;
        mBitRate = bitRate;
    }

    @Override
    protected int calcBitRate(final int frameRate) {
        int bitRate = mBitRate;
        if (bitRate == 0) {
            bitRate = super.calcBitRate(frameRate);
        }
        Log.i(TAG, "BitRate:" + bitRate);
        return bitRate;
    }

    private InputSurface mInputSurface;
    private OutputSurface mOutputSurface;

    @Override
    void prepare() throws IOException {
        if (DEBUG) Log.i(TAG, "prepare: ");
        mSurface = prepare_surface_encoder(MIME_TYPE, FRAME_RATE);

        mMediaCodec.start();
        mIsCapturing = true;

        //  test();

        new Thread(mScreenCaptureTask, "ScreenCaptureThread").start();
        if (DEBUG) Log.i(TAG, "prepare finishing");

        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Exception e) {
                Log.e(TAG, "prepare:", e);
            }
        }
    }

    MainHandler mMainHandler;

    class MainHandler extends Handler {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            try {
                drawFrame();
            } catch (Exception e) {
            }
        }
    }

    private final float[] mTmpMatrix = new float[16];

    private void drawFrame() throws IOException {

        mCameraTexture.updateTexImage();
        mCameraTexture.getTransformMatrix(mTmpMatrix);

        mEncoderSurface.makeCurrent();

        //GLES20.glViewport(0, 0, mWidth, mHeight);
        mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
        //drawExtra(mFrameNum, mWidth, mHeight);
        //mCircEncoder.frameAvailableSoon();

        frameAvailableSoon();

        mEncoderSurface.setPresentationTime(mCameraTexture.getTimestamp());
        mEncoderSurface.swapBuffers();

        //  mEncoderSurface.saveFrame(new File("/sdcard/test1.png"));
    }

    FullFrameRect mFullFrameBlit;
    int mTextureId;
    SurfaceTexture mCameraTexture;

    EglCore mEglCore;

    WindowSurface mEncoderSurface;

    OffscreenSurface offscreenSurface;

    public void test() throws IOException {

        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mEncoderSurface = new WindowSurface(mEglCore, mSurface, true);
        mEncoderSurface.makeCurrent();

        mFullFrameBlit = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));

        mTextureId = mFullFrameBlit.createTextureObject();
        mCameraTexture = new SurfaceTexture(mTextureId);
        mCameraTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.i(TAG, "onFrameAvailable:" + surfaceTexture);
                //mMainHandler.sendEmptyMessage(0);
            }
        });

        // mMainHandler = new MainHandler();

//        final Surface surface = new Surface(mCameraTexture);
//        final VirtualDisplay display = mMediaProjection.createVirtualDisplay(
//                "Capturing Display",
//                mWidth, mHeight, mDensity,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                surface, null, null);
    }

    @Override
    void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:");
        super.stopRecording();
        mIsCapturing = false;
    }

    private final Runnable mScreenCaptureTask = new Runnable() {
        @Override
        public void run() {

            try {
                test();
            } catch (IOException E) {
            }

            if (DEBUG) Log.d(TAG, "setup VirtualDisplay");
            for (; mIsCapturing; ) {
                synchronized (mSync) {
                    if (mIsCapturing && !mRequestStop && mRequestPause) {
                        try {
                            mSync.wait();
                        } catch (final InterruptedException e) {
                            break;
                        }
                        continue;
                    }
                }
                if (mIsCapturing && !mRequestStop && !mRequestPause) {

                    final Surface surface = new Surface(mCameraTexture);

                    final VirtualDisplay display = mMediaProjection.createVirtualDisplay(
                            "Capturing Display",
                            mWidth, mHeight, mDensity,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                            surface, null, null);
                    if (DEBUG) Log.v(TAG, "screen capture loop:display=" + display);
                    if (display != null) {
                        for (; mIsCapturing && !mRequestStop && !mRequestPause && !mIsEOS; ) {
                            synchronized (mSync) {
                                try {
                                    mSync.wait(40);

                                    drawFrame();

                                    //frameAvailableSoon();
                                } catch (final Exception e) {
                                    break;
                                }
                            }
                        }
                        frameAvailableSoon();
                        if (DEBUG) Log.v(TAG, "release VirtualDisplay");
                        display.release();
                    }
                }
            }
            if (DEBUG) Log.v(TAG, "tear down MediaProjection");
            if (mMediaProjection != null) {
                mMediaProjection.stop();
                mMediaProjection = null;
            }
            if (DEBUG) Log.v(TAG, "finished:");
        }
    };
}
