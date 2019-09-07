package org.cuba.neofit.gson;

import java.io.File;
import java.lang.reflect.Type;

import org.cuba.neofit.NeoConverter;
import org.cuba.neofit.NeoConverter.BodyFactory;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GsonBodyFactory implements BodyFactory {
    private Gson gson;
    
    public GsonBodyFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public NeoConverter<Object, RequestBody> requestConverter(Type parameterType, String contentType) {
        if(!contentType.isEmpty() && !contentType.contains("json") || parameterType.equals(File.class)) {
            return null;
        }
        
        return (object) -> {
            MediaType mediaType;
            
            if(contentType.isEmpty()) {
                mediaType = MediaType.parse("application/json");
            } else {
                mediaType = MediaType.parse(contentType);
            }
            
            return RequestBody.create(mediaType, gson.toJson(object, parameterType).getBytes());
        };
    }

    @Override
    public NeoConverter<byte[], Object> responseConverter(Type parameterType, String contentType) {
        if(!contentType.isEmpty() && !contentType.contains("json")) {
            return null;
        }
        
        return (bytes) -> {
            String json = new String(bytes);
            return gson.fromJson(json, parameterType);
        };
    }

}
