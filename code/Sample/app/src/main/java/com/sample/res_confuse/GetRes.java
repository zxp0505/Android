package com.sample.res_confuse;

import android.content.Context;
import android.content.res.Resources;

import com.sample.R;

import java.lang.reflect.Field;

/**
 * 资源会混淆，有些资源引用或访问方式在混淆后会失效，需要注意。
 */
public class GetRes {

    //在代码或资源中直接引用资源ID, 应该是不会有影响的。
    //要注意下面的两种方式：

    //1 不是修改源码混淆
    //2 R文件还是原ID, 没有混淆ID
    private int getStringResourceId(String fileName) {
        Field field = null;
        try {
            field = R.string.class.getField(fileName);
        } catch (NoSuchFieldException e) {
            return 0;
        }
        try {
            return field.getInt(null);
        } catch (IllegalAccessException e) {
        }

        return 0;
    }

    //读res文件夹及resources.arsc文件？？
    private int getResId(Context context, String name) {
        Resources resources = context.getResources();
        return resources.getIdentifier(name, "string", context.getPackageName());
    }
}
