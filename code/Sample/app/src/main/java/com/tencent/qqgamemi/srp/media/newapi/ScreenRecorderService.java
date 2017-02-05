package com.tencent.qqgamemi.srp.media.newapi;
/*
 * ScreenRecordingSample
 * Sample project to capture and save audio from internal and video from screen as MPEG4 file.
 *
 * Copyright (c) 2015 saki t_saki@serenegiant.com
 *
 * File name: ScreenRecorderService.java
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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;


import java.io.IOException;


@SuppressLint("NewApi")
public class ScreenRecorderService extends Service {
    private static final boolean DEBUG = true;
    private static final String TAG = "ScreenRecorderService";

    private static final String BASE = "com.serenegiant.service.ScreenRecorderService.";
    public static final String ACTION_START = BASE + "ACTION_START";
    public static final String ACTION_STOP = BASE + "ACTION_STOP";
    public static final String ACTION_PAUSE = BASE + "ACTION_PAUSE";
    public static final String ACTION_RESUME = BASE + "ACTION_RESUME";
    public static final String ACTION_QUERY_STATUS = BASE + "ACTION_QUERY_STATUS";
    public static final String ACTION_QUERY_STATUS_RESULT = BASE + "ACTION_QUERY_STATUS_RESULT";
    public static final String EXTRA_RESULT_CODE = BASE + "EXTRA_RESULT_CODE";
    public static final String EXTRA_QUERY_RESULT_RECORDING = BASE + "EXTRA_QUERY_RESULT_RECORDING";
    public static final String EXTRA_QUERY_RESULT_PAUSING = BASE + "EXTRA_QUERY_RESULT_PAUSING";

    /**
     * 开始录制时的参数：宽、高、码率、是否录声音
     */
    public static final String PARAM_WIDTH = BASE + "PARAM_WIDTH";
    public static final String PARAM_HEIGHT = BASE + "PARAM_HEIGHT";
    public static final String PARAM_BITRATE = BASE + "PARAM_BITRATE";
    public static final String PARAM_IS_RECORD_VOICE = BASE + "PARAM_IS_RECORD_VOICE";

    private final static Object sSync = new Object();
    private static MediaMuxerWrapper sMuxer;

    private MediaProjectionManager mMediaProjectionManager;

    public ScreenRecorderService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.v(TAG, "onCreate:");
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    protected void onHandleIntent(final Intent intent) {
        Log.i(TAG, "onHandleIntent:intent=" + intent);
        if (intent == null) return;

        final String action = intent.getStringExtra("action");
        Log.i(TAG, "onHandleIntent ====action" + action);
        if (ACTION_START.equals(action)) {
            Intent intent1 = intent;
            Intent intent2 = intent.getParcelableExtra("intent");
            intent1.putExtras(intent2);
            startScreenRecord(intent1);
            updateStatus();
        } else if (ACTION_STOP.equals(action)) {
            stopScreenRecord();
            updateStatus();
        } else if (ACTION_QUERY_STATUS.equals(action)) {
            updateStatus();
        } else if (ACTION_PAUSE.equals(action)) {
            pauseScreenRecord();
        } else if (ACTION_RESUME.equals(action)) {
            resumeScreenRecord();
        }
    }

    private void updateStatus() {
        final boolean isRecording, isPausing;
        synchronized (sSync) {
            isRecording = (sMuxer != null);
            isPausing = isRecording ? sMuxer.isPaused() : false;
        }

        final Intent result = new Intent();
        result.setAction(ACTION_QUERY_STATUS_RESULT);
        result.putExtra(EXTRA_QUERY_RESULT_RECORDING, isRecording);
        result.putExtra(EXTRA_QUERY_RESULT_PAUSING, isPausing);
        if (DEBUG)
            Log.v(TAG, "sendBroadcast:isRecording=" + isRecording + ",isPausing=" + isPausing);

        sendBroadcast(result);
    }

    /**
     * start screen recording as .mp4 file
     *
     * @param intent
     */
    private void startScreenRecord(final Intent intent) {
        if (DEBUG) Log.v(TAG, "startScreenRecord:sMuxer=" + sMuxer);
        synchronized (sSync) {
            if (sMuxer == null) {
                final int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
                // get MediaProjection
                final MediaProjection projection = mMediaProjectionManager.getMediaProjection(resultCode, intent);
                if (projection != null) {
                    final DisplayMetrics metrics = getResources().getDisplayMetrics();
                    final int density = metrics.densityDpi;

                    final int width = intent.getIntExtra(PARAM_WIDTH, metrics.widthPixels);
                    final int height = intent.getIntExtra(PARAM_HEIGHT, metrics.heightPixels);
                    final int bitRate = intent.getIntExtra(PARAM_BITRATE, 0);
                    final boolean isRecordVoice = intent.getBooleanExtra(PARAM_IS_RECORD_VOICE, true);

                    Log.i(TAG, "width:" + width);
                    Log.i(TAG, "height:" + height);
                    Log.i(TAG, "bitRate:" + bitRate);
                    Log.i(TAG, "isRecordVoice:" + isRecordVoice);

                    if (DEBUG) Log.v(TAG, "startRecording:");
                    try {
                        sMuxer = new MediaMuxerWrapper(".mp4");    // if you record audio only, ".m4a" is also OK.
                        if (true) {
                            // for screen capturing
                            new MediaScreenEncoder(sMuxer, mMediaEncoderListener,
                                    projection, width, height, density, bitRate);
                        }
                        if (isRecordVoice && MediaAudioEncoder.tryGetAudioRecord()) {
                            // for audio capturing
                            new MediaAudioEncoder(sMuxer, mMediaEncoderListener);
                        }
                        sMuxer.prepare();
                        sMuxer.startRecording();
                    } catch (final IOException e) {
                        Log.e(TAG, "startScreenRecord:", e);
                    }
                }
            }
        }
    }

    /**
     * stop screen recording
     */
    private void stopScreenRecord() {
        if (DEBUG) Log.v(TAG, "stopScreenRecord:sMuxer=" + sMuxer);
        synchronized (sSync) {
            if (sMuxer != null) {
                sMuxer.stopRecording();
                sMuxer = null;
                // you should not wait here
            }
        }
    }

    private void pauseScreenRecord() {
        synchronized (sSync) {
            if (sMuxer != null) {
                sMuxer.pauseRecording();
            }
        }
    }

    private void resumeScreenRecord() {
        synchronized (sSync) {
            if (sMuxer != null) {
                sMuxer.resumeRecording();
            }
        }
    }

    /**
     * callback methods from encoder
     */
    private static final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
        }
    };

    /**
     * Unless you provide binding for your service, you don't need to implement this
     * method, because the default implementation returns null.
     *
     * @see android.app.Service#onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
