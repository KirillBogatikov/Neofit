package org.kllbff.neofit;

import java.util.concurrent.Executor;

public abstract class NeoPlatform {
    public abstract Executor requestExecutor();
    public abstract Executor callbackExecutor();
}
