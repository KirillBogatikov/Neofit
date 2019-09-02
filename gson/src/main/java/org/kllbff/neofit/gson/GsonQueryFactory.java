package org.kllbff.neofit.gson;

import java.lang.reflect.Type;

import org.kllbff.neofit.NeoConverter;
import org.kllbff.neofit.NeoConverter.QueryFactory;

import com.google.gson.Gson;

public class GsonQueryFactory implements QueryFactory {
    private Gson gson;
    
    public GsonQueryFactory(Gson gson) {
        this.gson = gson;
    }
    
    @Override
    public NeoConverter<Object, String> converter(Type parameterType, String queryName) {
        return (object) -> {
            String json = gson.toJson(object, parameterType);
            return json.startsWith("\"") ? json.substring(1, json.length() - 1) : json;
        };
    }

}
