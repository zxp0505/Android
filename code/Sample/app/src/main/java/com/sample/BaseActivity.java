package com.sample;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by clarkehe on 1/19/16.
 * Todo:
 */
public class BaseActivity extends Activity {


    @Override
    protected void onPause() {
        super.onPause();

        try{
            dumpOverdraw();
        }
        catch (Exception e){}
    }

    private void dumpOverdraw() throws IOException {

        try {
            Class<?> view = Class.forName("android.view.View");

            Method methodGetHardwareRenderer = view.getDeclaredMethod("getHardwareRenderer");
            methodGetHardwareRenderer.setAccessible(true);
            Object Gl20Renderer = methodGetHardwareRenderer.invoke(getWindow().getDecorView());

            if (Gl20Renderer == null) {
                Log.d("过度绘制测试", " 异常，Gl20Renderer为空");
                return;
            }

            Field fieldDebugOverdrawLayer = Gl20Renderer.getClass().getSuperclass().getDeclaredField("mDebugOverdrawLayer");
            fieldDebugOverdrawLayer.setAccessible(true);
            Object GLES20RenderLayer = fieldDebugOverdrawLayer.get(Gl20Renderer);

            if (GLES20RenderLayer == null) {
                Log.d("过度绘制测试", " 异常，GLES20RenderLayer为空");
                return;
            }

            Method methodGetCanvas = GLES20RenderLayer.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getCanvas");
            methodGetCanvas.setAccessible(true);
            Object GLES20Canvas = methodGetCanvas.invoke(GLES20RenderLayer);

            Method methodGetOverdraw = Gl20Renderer.getClass().getDeclaredMethod("getOverdraw", Class.forName("android.view.HardwareCanvas"));
            methodGetOverdraw.setAccessible(true);
            Float result = (Float) methodGetOverdraw.invoke(Gl20Renderer, GLES20Canvas);

            String path = getClass().getPackage().getName() + "." + getClass().getSimpleName() + ".java";
            Log.d("过度绘制测试", "<gpuTest    name:[" + getClass().getSimpleName() + "] sum:[" + result + "]  route:[" + path +"]>");

        } catch (Exception e) {
            Log.d("过度绘制测试", " 异常");
        }
    }


}
