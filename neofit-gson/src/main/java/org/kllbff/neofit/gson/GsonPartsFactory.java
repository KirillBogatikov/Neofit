package org.kllbff.neofit.gson;

import java.io.File;
import java.lang.reflect.Type;

import org.kllbff.neofit.NeoConverter;
import org.kllbff.neofit.NeoConverter.PartsFactory;

import com.google.gson.Gson;

import okhttp3.MultipartBody.Part;

public class GsonPartsFactory implements PartsFactory {
    private Gson gson;
    
    public GsonPartsFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public NeoConverter<Object, Part> converter(Type parameterType, String contentType, String partName) {
       if(!contentType.isEmpty() && !contentType.contains("json") || parameterType.equals(File.class)) {
            return null;
        }
       
        return (object) -> {
            return Part.createFormData(partName, gson.toJson(object, parameterType));
        };
    }
    
}
