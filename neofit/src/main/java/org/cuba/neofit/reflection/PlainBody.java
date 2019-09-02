package org.cuba.neofit.reflection;

import java.lang.reflect.Type;

public class PlainBody {
    private String contentType;
    private Type parameterType;
    private int index;
    
    public PlainBody(String contentType, Type parameterType, int index) {
        this.contentType = contentType;
        this.parameterType = parameterType;
        this.index = index;
    }

    public String getContentType() {
        return contentType;
    }
    
    public Type getParameterType() {
        return parameterType;
    }

    public int getIndex() {
        return index;
    }
    
}
