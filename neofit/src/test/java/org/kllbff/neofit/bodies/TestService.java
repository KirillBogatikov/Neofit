package org.kllbff.neofit.bodies;

import java.io.File;

import org.kllbff.neofit.NeoCall;
import org.kllbff.neofit.annotations.Body;
import org.kllbff.neofit.annotations.FormItem;
import org.kllbff.neofit.annotations.Part;
import org.kllbff.neofit.annotations.Post;

public interface TestService {
    @Post("plain/octet")
    public NeoCall plainOctetStream(@Body byte[] bytes);

    @Post("plain/custom")
    public NeoCall plainCustomType(@Body(contentType = "application/custom") byte[] bytes);

    @Post("plain/png")
    public NeoCall plainPngBytes(@Body byte[] bytes);
    
    @Post("plain/file")
    public NeoCall plainFile(@Body File file);
    
    @Post("form/single")
    public NeoCall formSingle(@FormItem("first") byte[] bytes);

    @Post("form/several")
    public NeoCall formSeveral(@FormItem("first") byte[] bytes1, @FormItem("second") byte[] bytes2);
            
    @Post("parts/single")
    public NeoCall partsSingle(@Part("first") byte[] bytes);

    @Post("parts/several")
    public NeoCall partsSeveral(@Part("first") byte[] bytes1, @Part("second") byte[] bytes2);
    
    @Post("parts/octet")
    public NeoCall partsOctetStream(@Part("first") byte[] bytes);

    @Post("parts/custom")
    public NeoCall partsCustomType(@Part(value = "first", contentType = "application/custom") byte[] bytes);

    @Post("parts/png")
    public NeoCall partsBytesPng(@Part(value = "first") byte[] bytes);
    
    @Post("parts/file")
    public NeoCall partsFile(@Part(value = "first") File file);
}
