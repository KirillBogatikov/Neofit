package org.cuba.neofit;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Request;
import okio.Timeout;

public class NeoCall {
    private Executor requestExecutor, callbackExecutor;
    private ConverterManager converter;
    private Call call;
    private String syncLockerObject = "oh my god, only not dead-lock, please";
    private NeoResponse syncResponse;
    private IOException syncThrowable;
        
    public NeoCall(Call call, NeoPlatform platform, ConverterManager converter) {
        this.call = call;
        this.requestExecutor = platform.requestExecutor();
        this.callbackExecutor = platform.callbackExecutor();
        this.converter = converter;
    }
    
    public NeoResponse sync() throws IOException {
        syncThrowable = null;
        requestExecutor.execute(() -> { 
            try {
                syncResponse = new NeoResponse(call.execute(), converter);
            } catch (IOException e) {
                syncThrowable = e;
            }
            
            synchronized(syncLockerObject) {
                syncLockerObject.notifyAll();    
            }
        });
        
        synchronized(syncLockerObject) {
            try {
                syncLockerObject.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        
        if(syncThrowable != null) {
            throw syncThrowable;
        }
        
        return syncResponse;
    }
    
    public Request getHttpRequest() {
        return call.request();
    }
    
    public void async(NeoCallback callback) {
        requestExecutor.execute(() -> {
            try {
                NeoResponse response = sync();
                callbackExecutor.execute(() -> {
                    callback.onResponse(NeoCall.this, response);
                });
            } catch (Throwable e) {
                callbackExecutor.execute(() -> {
                    callback.onFailure(NeoCall.this, e);
                });
            }
        });
    }

    public boolean isExecuted() {
        return call.isExecuted();
    }
    
    public boolean isCancelled() {
        return call.isCanceled();
    }
    
    public Timeout timeout() {
        return call.timeout();
    }
    
    public void cancel() {
        call.cancel();
    }
}
