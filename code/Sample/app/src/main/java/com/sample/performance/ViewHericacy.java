package com.sample.performance;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;

/**
 * Created by clarkehe on 1/18/16.
 * Todo:
 */
public class ViewHericacy {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void trackViewTreeDepth (Application app)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            app.registerActivityLifecycleCallbacks(new ViewTreeTracer());
        }
    }
}
