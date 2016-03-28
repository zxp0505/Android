package com.sample.classLoader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

/**
 * Created by clarkehe on 3/24/16.
 * Todo:
 */
public class SampleActivity  extends Activity {

    private final static String TAG = "classLoader";

    public SampleActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Button(this));

        ClassLoader classLoader = getClassLoader();
        int i = 1;
        if (classLoader != null){
            Log.i(TAG, "[onCreate] classLoader " + i + " : " + classLoader.toString());
            while (classLoader.getParent()!=null){
                classLoader = classLoader.getParent();
                i += 1;
                Log.i(TAG,"[onCreate] classLoader " + i + " : " + classLoader.toString());
            }
        }
    }
}

/**
 *
 *
  classLoader 1 : dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.sample-1/base.apk"],nativeLibraryDirectories=[/data/app/com.sample-1/lib/arm, /vendor/lib, /system/lib]]]

  classLoader 2 : java.lang.BootClassLoader@8090c88
 */
