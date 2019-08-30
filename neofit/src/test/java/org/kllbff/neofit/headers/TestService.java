package org.kllbff.neofit.headers;

import org.kllbff.neofit.NeoCall;
import org.kllbff.neofit.annotations.Get;
import org.kllbff.neofit.annotations.Header;
import org.kllbff.neofit.annotations.Headers;

public interface TestService {
    @Get("headers/one")
    public NeoCall one(@Header("Test-Header") String test);
    
    @Get("headers/two")
    public NeoCall two(@Header("Test-First-Header") String first, @Header("Test-Second-Header") String second);
    
    @Get("headers/several")
    @Headers({
        "Test-First-Header: first",
        "Test-Second-Header: second"
    })
    public NeoCall several();
}
