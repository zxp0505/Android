package com.sample.hotfix.dex;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sample.R;

/**
 * 在虚拟机启动的时候，当verify选项被打开的时候，如果static方法、private方法、构造函数等，
 * 其中的直接引用（第一层关系）到的类都在同一个dex文件中，那么该类就会被打上CLASS_ISPREVERIFIED标志。
 * <p>
 * 打上这个标识的作用：跟类优化有关？？？
 */
public class HotFixActivity extends Activity {

    private final static String TAG = "HotFixActivity";

    /**
     * 08-15 16:16:36.269 4276-4276/? W/dalvikvm: Class resolved by unexpected DEX: Lcom/sample/hotfix/dex/HotFixActivity;(0x42691000):0x751cc000 ref [Lcom/sample/hotfix/dex/BugClass;] Lcom/sample/hotfix/dex/BugClass;(0x42691000):0x7505c000
     * 08-15 16:16:36.269 4276-4276/? W/dalvikvm: (Lcom/sample/hotfix/dex/HotFixActivity; had used a different Lcom/sample/hotfix/dex/BugClass; during pre-verification)
     * <p>
     * 08-15 16:16:36.269 4276-4276/? E/AndroidRuntime: FATAL EXCEPTION: main
     * Process: com.sample, PID: 4276
     * java.lang.IllegalAccessError: Class ref in pre-verified class resolved to unexpected implementation
     * at com.sample.hotfix.dex.HotFixActivity.onCreate(HotFixActivity.java:20)
     * at android.app.Activity.performCreate(Activity.java:5231)
     * <p>
     * 希望BugClass这个类与HotFixActivity，应该是在一个DEX文件，
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printClassLoad();
        setContentView(R.layout.activity_hot_fix);
        TextView view = (TextView) findViewById(R.id.textView);
        view.setText(new BugClass().getStr());
    }


    /**
     * 08-15 16:16:36.259 4276-4276/? I/HotFixActivity: [onCreate] classLoader 1 : dalvik.system.PathClassLoader[DexPathList[[dex file "dalvik.system.DexFile@426939b0", zip file "/data/app/com.sample-1.apk"],nativeLibraryDirectories=[/data/app-lib/com.sample-1, /vendor/lib, /system/lib]]]
     * 08-15 16:16:36.259 4276-4276/? I/HotFixActivity: [onCreate] classLoader 1 : java.lang.BootClassLoader@41636fd0
     */

    void printClassLoad() {
        ClassLoader classLoader = getClassLoader();
        int i = 1;
        if (classLoader != null) {
            Log.i(TAG, "[onCreate] classLoader " + i + " : " + classLoader.toString());

            while (classLoader.getParent() != null) {
                classLoader = classLoader.getParent();
                Log.i(TAG, "[onCreate] classLoader " + i + " : " + classLoader.toString());
            }
        }
    }
}