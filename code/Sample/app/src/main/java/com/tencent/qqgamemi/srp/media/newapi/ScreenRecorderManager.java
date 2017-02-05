package com.tencent.qqgamemi.srp.media.newapi;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenRecorderManager {

    //private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;

    private static ScreenRecorderManager mScreenRecorderMgr;
    private static String TAG = "ScreenRecorderManager";

    private String mPath;
    // 定时检测的数据信息
    private Timer mTimerDetection;
    private TimerTask mTimerTaskDetection;
    private final static int TIME_SCAN_DISTANCE = 100; // 扫描间隔时间
    private String mLastFrameAppPkgName;

    public static ScreenRecorderManager getInstance() {
        if (mScreenRecorderMgr == null) {
            mScreenRecorderMgr = new ScreenRecorderManager();
        }
        return mScreenRecorderMgr;
    }

//    /**
//     * 开启录屏
//     */
//    @SuppressLint("NewApi")
//    public void startRecord(FloatPanel floatPanel,Context context) {
//        LogUtil.i(TAG, "ScreenRecorderManager  startRecord  ---");
//        final MediaProjectionManager manager = (MediaProjectionManager)context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        final Intent permissionIntent = manager.createScreenCaptureIntent();
//        //((Activity)context).startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);
//        //floatPanel.startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);
//        floatPanel.startActivityForResult(false,permissionIntent,REQUEST_CODE_SCREEN_CAPTURE);
//        LogUtil.i(TAG, "ScreenRecorderManager  startRecord --end");
//    }


//	/**
//	 * startActivityForResult的回调
//	 * @param context
//	 * @param requestCode
//	 * @param resultCode
//	 * @param data
//	 * @return
//	 */
//	public boolean onActivityResult(Context context, int requestCode, final int resultCode, final Intent data, String pkgName) {
//		Log.v(TAG, "onActivityResult:resultCode=" + resultCode + ",data=" + data);
//		if (REQUEST_CODE_SCREEN_CAPTURE == requestCode) {
//            if (resultCode != Activity.RESULT_OK) {
//                // when no permission
//              //  Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
//                return false;
//            }
//            startScreenRecorder(context, resultCode, data);
//    		startDetectionTimer(context, pkgName);
//        }
//
//		return true;
//	}

    /**
     * 关闭屏幕录屏
     *
     * @param context
     */
    public void stopRecord(Context context) {

        final Intent intent = new Intent(context, ScreenRecorderService.class);
        Bundle bundle = new Bundle();
        bundle.putString("action", ScreenRecorderService.ACTION_STOP);
        intent.setAction(ScreenRecorderService.ACTION_STOP);
        intent.putExtras(bundle);
        context.startService(intent);

        //Bundle bundle = new Bundle();
        //bundle.putString("action", ScreenRecorderService.ACTION_STOP);
        //SCRPlugin.getInstance().getPluginHelper().startLeafService(context, SCRPlugin.getInstance().getScrPluginInfo(), "com.tencent.qqgamemi.srp.media.newapi.ScreenRecorderService", bundle);

        stopDetectionTimer();
    }

    /**
     * 停止检测
     */
    private void stopDetectionTimer() {
        if (mTimerTaskDetection != null) {
            mTimerTaskDetection.cancel();
            mTimerTaskDetection = null;
        }
        if (mTimerDetection != null) {
            mTimerDetection.cancel();
            mTimerDetection = null;
        }
    }

    public void setRecordPath(String path) {
        this.mPath = path;
    }

    /**
     * 获取录屏的路径
     *
     * @return
     */
    public String getRecordPath() {
        return mPath;
    }

    private void startScreenRecorder(Context context, final int resultCode, final Intent data) {

        Log.i(TAG, "startScreenRecorder------------");

        /**
         final Intent intent = new Intent(context, ScreenRecorderService.class);
         intent.setAction(ScreenRecorderService.ACTION_START);
         intent.putExtra(ScreenRecorderService.EXTRA_RESULT_CODE, resultCode);
         intent.putExtras(data);
         context.startService(intent);
         */

        //final VideoQuality quality = RecordSettings.getInstance().getVideoQuality();
        //final int orientation = ReflectOrientationUtil.getScreenOrientation(SCRPlugin.getInstance().getContext());

        /** 如果屏幕的宽高比与视频视频的宽度比不一致，会自动加黑边，保证不会变形。*/
        int width = 540;//quality.videoWidth;
        int height = 960;//quality.videoHeight;

        /** 竖屏时 */
//        if (orientation == RecordAgent.ORIENTATION_PORTRAIT || orientation == 9){
//            width = quality.videoHeight;
//            height = quality.videoWidth;
//        }

        final int bitRate = 1200000;//AVPolicy.getBitRate(quality.type);
        final int isAudioOpen = 1;//RecordSettings.getInstance().isAudioOpen();

        Log.i(TAG, "Width:" + width);
        Log.i(TAG, "Height:" + height);
        Log.i(TAG, "bitRate:" + bitRate);
        Log.i(TAG, "isAudioOpen:" + isAudioOpen);

        Bundle bundle = new Bundle();
        bundle.putInt(ScreenRecorderService.EXTRA_RESULT_CODE, resultCode);
        bundle.putParcelable("intent", data);
        bundle.putString("action", ScreenRecorderService.ACTION_START);

        bundle.putInt(ScreenRecorderService.PARAM_WIDTH, width);
        bundle.putInt(ScreenRecorderService.PARAM_HEIGHT, height);
        bundle.putInt(ScreenRecorderService.PARAM_BITRATE, bitRate);
        bundle.putBoolean(ScreenRecorderService.PARAM_IS_RECORD_VOICE, isAudioOpen == 1);


        final Intent intent = new Intent(context, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_START);
        intent.putExtras(bundle);
        //intent.putExtra(ScreenRecorderService.EXTRA_RESULT_CODE, resultCode);
        //intent.putExtras(data);
        context.startService(intent);

        //SCRPlugin.getInstance().getPluginHelper().startLeafService(context, SCRPlugin.getInstance().getScrPluginInfo(),"com.tencent.qqgamemi.srp.media.newapi.ScreenRecorderService",bundle);
        Log.i(TAG, "startScreenRecorder------------");
    }

    public void startRecord(Context context, final int resultCode, final Intent data) {
        startScreenRecorder(context, resultCode, data);
        //startDetectionTimer(context, "com.tencent.pao");
    }
}
