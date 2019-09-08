package org.cuba.neofit;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

public abstract class NeoPlatform {
    public abstract Executor requestExecutor();
    public abstract Executor callbackExecutor();
    public abstract boolean isDefault(Method method);
}
