package org.kllbff.neofit.converters;

import org.kllbff.neofit.NeoCall;
import org.kllbff.neofit.annotations.Get;

public interface TestService {
    @Get("string")
    public NeoCall stringValue();

    @Get("byte")
    public NeoCall byteValue();

    @Get("short")
    public NeoCall shortValue();
    
    @Get("integer")
    public NeoCall integerValue();

    @Get("long")
    public NeoCall longValue();

    @Get("float")
    public NeoCall floatValue();
    
    @Get("double")
    public NeoCall doubleValue();
    
    @Get("bigdecimal")
    public NeoCall bigDecimalValue();

    @Get("boolean")
    public NeoCall booleanValue();

    @Get("char")
    public NeoCall charValue();
    
    @Get("octal")
    public NeoCall octal();
    
    @Get("hexademical")
    public NeoCall hex();

    @Get("binary")
    public NeoCall bin();
    
    @Get("serializable")
    public NeoCall serializableValue();
}