package org.kllbff.neofit.reflection;

import java.lang.reflect.Type;

public class FormDataItem {
    private String name;
    private Type parameterType;
    private int index;
    
    public FormDataItem(String name, Type parameterType, int index) {
        this.name = name;
        this.parameterType = parameterType;
        this.index = index;
    }

    public String getName() {
        return name;
    }
    
    public Type getParameterType() {
        return parameterType;
    }
    
    public int getIndex() {
        return index;
    }
    
}
