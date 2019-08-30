package org.kllbff.neofit.reflection;

import java.lang.reflect.Type;

public class RequestHeader {
    private String name;
    private String value;
    private Type parameterType;
    private int index;
    
    public RequestHeader(String name, String value, Type parameterType, int index) {
        this.name = name;
        this.value = value;
        this.parameterType = parameterType;
        this.index = index;
    }
    
    public RequestHeader(String name, String value) {
        this(name, value, null, -1);
    }
    
    public RequestHeader(String name, Type parameterType, int index) {
        this(name, null, parameterType, index);
    }
    
    public boolean isConstant() {
        return index == -1;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
    public Type getParameterType() {
        return parameterType;
    }
    
    public int getIndex() {
        return index;
    }

}
