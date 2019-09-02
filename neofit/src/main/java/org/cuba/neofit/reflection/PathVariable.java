package org.cuba.neofit.reflection;

public class PathVariable {
    private String var;
    private int index;
    
    public PathVariable(String var, int index) {
        this.var = var;
        this.index = index;
    }
    
    public String getVar() {
        return var;
    }
    
    public int getIndex() {
        return index;
    }
    
}
