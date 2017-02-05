package com.sample.record;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by clarkehe on 3/24/16.
 * 5.0及以上申请录屏的权限
 */
public class ScreenCapture {

    private final static String TAG = "ScreenCapture";
    private final static int REQUEST_CODE_SCREEN_CAPTURE = 1;

    private static boolean sGetPermissionSuccess = false;
    private static Runnable sRunnable;

    private static int sResultCode = 0;
    private static Intent sData = null;

    public static class TemActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestScreenCapture(this);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.i(TAG, "onActivityResult, requestCode:" + requestCode + ", resultCode:" + requestCode);
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
                if (resultCode == Activity.RESULT_OK) {
                    sGetPermissionSuccess = true;

                    sResultCode = resultCode;
                    sData = data;

                    new android.os.Handler().post(sRunnable);

                    // SDKApiHelper.getInstance().runOnMainThread(sRunnable);
                    // sRunnable = null;

                    // QmiSdkApi.setScreenCaptureIntent(resultCode, data);
                }
            }
            finish();
        }
    }

    public static boolean requestPermission(Context context, Runnable runnable) {
        if (sGetPermissionSuccess) return true;
        sRunnable = runnable;

        final Intent intent = new Intent(context, TemActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return false;
    }

    public static int gerResultCode() {
        return sResultCode;
    }

    public static Intent getData() {
        return sData;
    }

    @TargetApi(21)
    static void requestScreenCapture(final Activity context) {
        final MediaProjectionManager manager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final Intent permissionIntent = manager.createScreenCaptureIntent();
        context.startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }
}
