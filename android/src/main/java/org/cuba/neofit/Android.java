package org.cuba.neofit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Android extends NeoPlatform {
    private Executor requestExecutor, callbackExecutor;
    private int apiVersionCode;
    
    public Android() {
        requestExecutor = Executors.newFixedThreadPool(4);
        
        try {
            Class<?> handlerClass = Class.forName("android.os.Handler");
            Class<?> looperClass = Class.forName("android.os.Looper");
            
            Constructor<?> handlerConstructor = handlerClass.getDeclaredConstructor(looperClass);
            Method handlerPost = handlerClass.getDeclaredMethod("post", Runnable.class);
            Method getMainLooper = looperClass.getDeclaredMethod("getMainLooper");
            final Object handlerInstance = handlerConstructor.newInstance(getMainLooper.invoke(null));
            
            callbackExecutor = (command) -> {
                try {
                    handlerPost.invoke(handlerInstance, command);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        
        try {
            Class<?> version = Class.forName("android.os.Build.VERSION");
            Field sdkIntField = version.getDeclaredField("SDK_INT");
            apiVersionCode = (int)(sdkIntField.get(null));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Executor requestExecutor() {
        return requestExecutor;
    }

    @Override
    public Executor callbackExecutor() {
        return callbackExecutor;
    }

    @Override
    public boolean isDefault(Method method) {
        if(apiVersionCode >= 24) {
            return method.isDefault();
        }
        return false;
    }

}
