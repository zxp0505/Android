package com.sample.performance;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectHelper {
    
    public static Object getField(Class<?> clazz, String fieldName, Object instance) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object getField(String className, String fieldName, Object instance) {
        try {
            Class<?> clazz = Class.forName(className);
            return getField(clazz, fieldName, instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void setField(Class<?> clazz, String fieldName, Object value, Object instance) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void setField(String className, String fieldName, Object value, Object instance) {
        try {
            Class<?> clazz = Class.forName(className);
            setField(clazz, fieldName, value, instance);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
    
    public static Object invokeMethod(Class<?> clazz, String methodName, Object instance, Class<?>[] paramTypes, Object[] params) {
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(instance, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object invokeMethod(String className, String methodName, Object instance, Class<?>[] paramTypes, Object[] params) {
        try {
            Class<?> clazz = Class.forName(className);
            return invokeMethod(clazz, methodName, instance, paramTypes, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
	public static long getInstanceCount(Class<?> find) {
    	long start = System.currentTimeMillis();
		long result = 0;
		try {
			Class<?> clazz = Class.forName("dalvik.system.VMDebug");
			Method method = clazz.getMethod("countInstancesOfClass", Class.class, boolean.class);
			result = (Long) method.invoke(null, find, false);
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		Log.e("fuck", "use time===" + (System.currentTimeMillis() - start));
		return result;
	}
}
