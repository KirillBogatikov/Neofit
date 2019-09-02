package org.cuba.neofit;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface NeoConverter<I, O> {
    public static interface Factory {};
    
    public static interface QueryFactory extends Factory {
        public NeoConverter<Object, String> converter(Type parameterType, String queryName);
    }
    
    public static interface HeaderFactory extends Factory {
        public NeoConverter<Object, String> converter(Type parameterType, String headerName);
    }
    
    public static interface BodyFactory extends Factory {
        public NeoConverter<Object, RequestBody> requestConverter(Type parameterType, String contentType);
        public NeoConverter<byte[], Object> responseConverter(Type parameterType, String contentType);
    }
    
    public static interface FormDataFactory extends Factory {
        public NeoConverter<Object, String> converter(Type parameterType, String itemName);
    }
    
    public static interface PartsFactory extends Factory {
        public NeoConverter<Object, MultipartBody.Part> converter(Type parameterType, String contentType, String partName);
    }
    
    public O convert(I source);
}
