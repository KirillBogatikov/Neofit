package org.kllbff.neofit;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Response;

public class NeoResponse {
    private Response<?> httpResponse;
    private ResponseBody httpResponseBody;
    private byte[] httpResponseBodyBytes;
    private List<Factory> factories;
    
    public NeoResponse(Response<?> response, List<Converter.Factory> factories) throws IOException {
        this.httpResponse = response;
        if(response.code() < 300) {
            httpResponseBody = ((ResponseBody)response.body());
        } else {
            httpResponseBody = response.errorBody();
        }
        this.factories = factories;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T value(Type type) throws IOException {
        Converter<ResponseBody, ?> converter = null;
        for(Factory factory : factories) {
            try {
                converter = factory.responseBodyConverter(type, null, null);
            } catch(NullPointerException e) {
                //not Neo-Converter
            }
        }
        
        if(converter != null) {
            if(httpResponseBody.contentLength() == 0) {
                return null;
            }
            return (T)converter.convert(copyBody());
        }
        throw new NullPointerException("Converter for " + type + " not found");
    }
    
    private byte[] bytes() throws IOException {
        if(httpResponseBodyBytes == null) {
            httpResponseBodyBytes = httpResponseBody.bytes();
        }
        return httpResponseBodyBytes;
    }
    
    private ResponseBody copyBody() throws IOException {
        return ResponseBody.create(httpResponseBody.contentType(), bytes());
    }
    
    public byte[] raw() throws IOException {
        return copyBody().bytes();
    }
    
    public String string() throws IOException {
        return copyBody().string();
    }
    
    public int code() {
        return httpResponse.code();
    }
    
    public boolean isInformation() {
        return httpResponse.code() > 99 && httpResponse.code() < 200;
    }
    
    public boolean isSuccess() {
        return httpResponse.code() > 199 && httpResponse.code() < 300;
    }
    
    public boolean isError() {
        return httpResponse.code() > 299 && httpResponse.code() < 400;
    }
    
    public boolean isServerError() {
        return httpResponse.code() > 499 && httpResponse.code() < 600;
    }
}
