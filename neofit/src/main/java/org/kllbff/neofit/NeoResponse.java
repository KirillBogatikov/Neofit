package org.kllbff.neofit;

import java.io.IOException;
import java.lang.reflect.Type;

import org.kllbff.neofit.exceptions.NeofitException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NeoResponse {
    private Response httpResponse;
    private MediaType contentType;
    private byte[] bodyBytes;
    private ConverterManager converter;
    
    public NeoResponse(Response response, ConverterManager converter) throws IOException {
        this.httpResponse = response;
        try(ResponseBody body = response.body()) {
            contentType = body.contentType();
            bodyBytes = body.bytes();
        }
        this.converter = converter;
    }
    
    public Response getHttpResponse() {
        return httpResponse;
    }
    
    public ResponseBody getHttpBody() {
        return ResponseBody.create(contentType, bodyBytes);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T to(Type type) throws NeofitException {
        return (T)converter.parseResponseBody(type, contentType == null ? null : contentType.toString(), bodyBytes);
    }
    
    public int code() {
        return httpResponse.code();
    }
    
    public boolean isInfo() {
        return code() > 99 && code() < 200;
    }
    
    public boolean isSuccess() {
        return code() > 199 && code() < 300;
    }
    
    public boolean isRedirect() {
        return code() > 299 && code() < 400;
    }
    
    public boolean isError() {
        return code() > 399 && code() < 500;
    }
    
    public boolean isServerError() {
        return code() > 499 && code() < 600;
    }
}
