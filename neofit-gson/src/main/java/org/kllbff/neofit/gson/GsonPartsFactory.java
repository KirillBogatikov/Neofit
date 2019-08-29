package org.kllbff.neofit.gson;

import java.lang.reflect.Type;

import org.kllbff.neofit.NeoConverter;
import org.kllbff.neofit.NeoConverter.PartsFactory;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.MultipartBody.Part;

public class GsonPartsFactory implements PartsFactory {
    private Gson gson;
    
    public GsonPartsFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public NeoConverter<Object, Part> converter(Type parameterType, String contentType, String partName) {
        if(!contentType.isEmpty() || !contentType.contains("json")) {
            return null;
        }
        
        return (object) -> {
            MediaType mediaType;
            
            if(contentType.isEmpty()) {
                mediaType = MediaType.parse("application/json");
            } else {
                mediaType = MediaType.parse(contentType);
            }
            
            return Part.create(RequestBody.create(mediaType, gson.toJson(object, parameterType).getBytes()));
        };
    }
    
}
