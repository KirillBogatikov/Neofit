package org.kllbff.neofit.url;

import org.kllbff.neofit.NeoCall;
import org.kllbff.neofit.annotations.Get;
import org.kllbff.neofit.annotations.Path;
import org.kllbff.neofit.annotations.Post;
import org.kllbff.neofit.annotations.Query;
import org.kllbff.neofit.annotations.Request;
import org.kllbff.neofit.annotations.Service;

@Service("dir/sub0")
public interface TestService {
    @Get("{path1}/{path2}")
    public NeoCall path(@Path("path1") String path1, @Path("path2") String path2);
    
    @Post("")
    public NeoCall queries(@Query("query1") String query1, @Query("query2") String query2);
    
    @Request(url = "sub1/sub2/{id}/sub4/{id1}", method = "complex")
    public NeoCall complex(@Path("id") String id, @Path("id1") String id1, @Query("query1") String query1, @Query("query2") String query2);
}
