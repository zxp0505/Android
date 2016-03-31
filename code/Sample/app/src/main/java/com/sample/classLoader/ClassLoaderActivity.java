package com.sample.classLoader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ClassLoaderActivity extends Activity {

    private final static String TAG = "classLoader";

    public ClassLoaderActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView view = new TextView(this);
        setContentView(view);

        String classLoaderStr = "Current Activity ClassLoader:";
        ClassLoader classLoader = getClassLoader();

        int i = 1;
        if (classLoader != null){
            Log.i(TAG, "[onCreate] classLoader " + i + " : " + classLoader.toString());

            classLoaderStr += "\r\n\r\n";
            classLoaderStr += (i + ". ");
            classLoaderStr += classLoader.toString();

            while (classLoader.getParent()!=null){
                classLoader = classLoader.getParent();

                i += 1;
                Log.i(TAG,"[onCreate] classLoader " + i + " : " + classLoader.toString());

                classLoaderStr += "\r\n\r\n";
                classLoaderStr += (i + ". ");
                classLoaderStr += classLoader.toString();
            }
        }
        view.setText(classLoaderStr);
    }
}

/**
 *
  classLoader 1 : dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.sample-1/base.apk"],nativeLibraryDirectories=[/data/app/com.sample-1/lib/arm, /vendor/lib, /system/lib]]]
  classLoader 2 : java.lang.BootClassLoader@8090c88
 */
