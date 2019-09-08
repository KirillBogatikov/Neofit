package org.cuba.neofit;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.cuba.neofit.DefaultConverter.DefaultBodyFactory;
import org.cuba.neofit.DefaultConverter.DefaultFormDataFactory;
import org.cuba.neofit.DefaultConverter.DefaultHeaderFactory;
import org.cuba.neofit.DefaultConverter.DefaultPartsFactory;
import org.cuba.neofit.DefaultConverter.DefaultQueryFactory;
import org.cuba.neofit.NeoConverter.BodyFactory;
import org.cuba.neofit.NeoConverter.FormDataFactory;
import org.cuba.neofit.NeoConverter.HeaderFactory;
import org.cuba.neofit.NeoConverter.PartsFactory;
import org.cuba.neofit.NeoConverter.QueryFactory;
import org.cuba.neofit.exceptions.NeofitException;
import org.cuba.neofit.reflection.ServiceProxy;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Neofit {
    public static class Builder {
        private List<QueryFactory> queryFactories;
        private List<BodyFactory> bodyFactories;
        private List<PartsFactory> partsFactories;
        private List<HeaderFactory> headerFactories;
        private List<FormDataFactory> formFactories;
        private HttpUrl baseUrl;
        private OkHttpClient client;
        private NeoPlatform platform;
        
        public Builder() {
            queryFactories = new ArrayList<>();
            bodyFactories = new ArrayList<>();
            partsFactories = new ArrayList<>();
            headerFactories = new ArrayList<>();
            formFactories = new ArrayList<>();
        }
        
        public Builder platform(NeoPlatform platform) {
            this.platform = platform;
            return this;
        }
        
        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }
        
        public Builder addQueryFactory(QueryFactory factory) {
            queryFactories.add(factory);
            return this;
        }
        
        public Builder addBodyFactory(BodyFactory factory) {
            bodyFactories.add(factory);
            return this;
        }
        
        public Builder addPartsFactory(PartsFactory factory) {
            partsFactories.add(factory);
            return this;
        }
        
        public Builder addHeaderFactory(HeaderFactory factory) {
            headerFactories.add(factory);
            return this;
        }
        
        public Builder addFormDataFactory(FormDataFactory factory) {
            formFactories.add(factory);
            return this;
        }
        
        public Builder baseUrl(String url) {
            this.baseUrl = HttpUrl.parse(url);
            return this;
        }
        
        public Neofit build() {
            if(baseUrl == null) {
                throw new IllegalArgumentException("Base URL should be specified by baseUrl()");
            }
            
            Neofit neofit = new Neofit();      
            queryFactories.add(new DefaultQueryFactory());
            headerFactories.add(new DefaultHeaderFactory());
            bodyFactories.add(new DefaultBodyFactory());
            formFactories.add(new DefaultFormDataFactory());
            partsFactories.add(new DefaultPartsFactory());
            
            neofit.converter = new ConverterManager(queryFactories, headerFactories, bodyFactories, formFactories, partsFactories);
            
            if(client == null) {
                client = new OkHttpClient();
            }
            neofit.client = client;
            
            if(platform == null) {
                platform = new NeoPlatform() {
                    private Executor requestExecutor = Executors.newFixedThreadPool(4);
                    private Executor callbackExecutor = (command) -> { command.run(); };
                                        
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
                        return method.isDefault();
                    }
                    
                };
            }
            neofit.platform = platform;
            
            neofit.baseUrl = baseUrl;
            
            return neofit;
        }
    }
    
    private ConverterManager converter;
    private OkHttpClient client;
    private HttpUrl baseUrl;
    private NeoPlatform platform;
    
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type) throws NeofitException {
        if(!type.isInterface()) {
            throw new IllegalArgumentException("Service should be interface");
        }
        
        ServiceProxy proxy = new ServiceProxy(type, baseUrl, platform, client, converter);
        return (T)Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{ type }, proxy);
    }
}
