package org.kllbff.neofit;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class Neofit {
    private Retrofit retrofit;
    private List<Converter.Factory> factories;
    
    Neofit(Retrofit retrofit, List<Converter.Factory> factories) {
        this.retrofit = retrofit;
        this.factories = Collections.unmodifiableList(factories);
    }
    
    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
    
    public okhttp3.Call.Factory callFactory() {
        return retrofit.callFactory();
    }
    
    public HttpUrl baseUrl() {
        return retrofit.baseUrl();
    }
    
    public List<CallAdapter.Factory> callAdapterFactories() {
        return retrofit.callAdapterFactories();
    }
    
    public List<Converter.Factory> converterFactories() {
        return factories;
    }
    
    public Executor callbackExecutor() {
        return retrofit.callbackExecutor();
    }
    
    public NeoCall neo(Call<?> call) {
        return new NeoCall(call, factories);
    }
    
    public static class Builder {
        private Retrofit.Builder retrofitBuilder;
        private List<Converter.Factory> retrofitConverterFactories;
        
        public Builder() {
            retrofitBuilder = new Retrofit.Builder();
            retrofitConverterFactories = new ArrayList<>();
        }
        
        public Builder client(OkHttpClient client) {
            retrofitBuilder.client(client);
            return this;
        }
        
        public Builder callFactory(okhttp3.Call.Factory factory) {
            retrofitBuilder.callFactory(factory);
            return this;
        }
        
        public Builder baseUrl(URL baseUrl) {
            retrofitBuilder.baseUrl(baseUrl);
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            retrofitBuilder.baseUrl(baseUrl);
            return this;
        }
        
        public Builder baseUrl(HttpUrl baseUrl) {
            retrofitBuilder.baseUrl(baseUrl);
            return this;
        }
        
        public Builder addConverterFactory(Converter.Factory factory) {
            retrofitConverterFactories.add(factory);
            return this;
        }
        
        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            retrofitBuilder.addCallAdapterFactory(factory);
            return this;
        }
        
        public Builder callbackExecutor(Executor executor) {
            retrofitBuilder.callbackExecutor(executor);
            return this;
        }
        
        public List<CallAdapter.Factory> callAdapterFactories() {
            return retrofitBuilder.callAdapterFactories();
        }
            
        public List<Converter.Factory> converterFactories() {
            return retrofitConverterFactories;
        }
                
        public Builder validateEagerly(boolean validateEagerly) {
            retrofitBuilder.validateEagerly(validateEagerly);
            return this;
        }
        
        public Neofit build() {
            retrofitBuilder.addConverterFactory(new NeoConverterFactory(retrofitConverterFactories));
            return new Neofit(retrofitBuilder.build(), retrofitConverterFactories);
        }
    }
}
