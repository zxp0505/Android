package com.sample.record;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by clarkehe on 5/4/16.
 * Todo:
 */
public class RecordUtil {

    public static final String getVideoPath() {
        return getVideoRootPath() + "ScreenRecord";
    }

    public static final String getVideoRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Tencent/shouyoubao/";
    }

    public static String getRecordVideoPath() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = sDateFormat.format(new Date(System.currentTimeMillis())) + "-"
                + "test";

        return getVideoPath() + "/" + fileName + ".mp4";
    }
}
