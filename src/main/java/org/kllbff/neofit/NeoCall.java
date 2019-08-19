package org.kllbff.neofit;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class NeoCall {
    private Call<? extends Object> call;
    private Response<? extends Object> response;
    private List<Converter.Factory> factories;
    
    public NeoCall(Call<? extends Object> call, List<Converter.Factory> factories) {
        this.call = call;
        this.factories = factories;
    }
    
    public NeoResponse sync() throws IOException {
        if(isExecuted()) {
            throw new IllegalStateException("Already executed");
        }
        
        response = call.execute();
        return new NeoResponse(response, factories);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void async(NeoCallback callback) {
        if(isExecuted()) {
            throw new IllegalStateException("Already executed");
        }
        
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                NeoCall.this.response = response;
                try {
                    callback.onResponse(NeoCall.this, new NeoResponse(response, factories));
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
            
            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onFailure(NeoCall.this, t);
            }
        });
    }

    public boolean isExecuted() {
        return call.isExecuted();
    }

    public Request request() {
        return call.request();
    }

    @SuppressWarnings("rawtypes")
    public Response response() {
        return response;
    }

    public void cancel() {
        call.cancel();
    }

    public boolean isCancelled() {
        return call.isCanceled();
    }
}
