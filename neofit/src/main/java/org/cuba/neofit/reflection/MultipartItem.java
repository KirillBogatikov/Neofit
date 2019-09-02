package org.cuba.neofit.reflection;

import java.lang.reflect.Type;

public class MultipartItem {
    private String name;
    private String contentType;
    private Type parameterType;
    private int index;
    
    public MultipartItem(String name, String contentType, Type parameterType, int index) {
        this.name = name;
        this.contentType = contentType;
        this.parameterType = parameterType;
        this.index = index;
    }

    public String getName() {
        return name;
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
