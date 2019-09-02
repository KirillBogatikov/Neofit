package org.cuba.neofit.converters;

import java.io.Serializable;

public class SerializableItem implements Serializable {
    private static final long serialVersionUID = -6273870872707780714L;

    private int id;
    private String name;
    
    public SerializableItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object object) {
        if(object instanceof SerializableItem) {
            SerializableItem item = (SerializableItem)object;
            
            return item.id == id && item.name.equals(name);
        }
        
        return false;
    } 

}
