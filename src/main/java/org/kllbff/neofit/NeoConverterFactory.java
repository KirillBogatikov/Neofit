package org.kllbff.neofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

public final class NeoConverterFactory extends Factory {
    private List<Converter.Factory> retrofitConverterFactories;
    
    NeoConverterFactory(List<Converter.Factory> retrofitConverterFactories) {
        this.retrofitConverterFactories = retrofitConverterFactories;
    }
    
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new Converter<ResponseBody, Object>() {

            @Override
            public Object convert(ResponseBody value) throws IOException {
                return value;
            }
            
        };
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        for(Factory factory : retrofitConverterFactories) {
            Converter<?, RequestBody> converter = factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
            if(converter != null) {
                return converter;
            }
        }
        return null;
    }
}
