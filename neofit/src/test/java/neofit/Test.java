package neofit;

import java.io.IOException;
import java.lang.reflect.Type;

import org.kllbff.neofit.NeoCall;
import org.kllbff.neofit.NeoConverter;
import org.kllbff.neofit.NeoConverter.QueryFactory;
import org.kllbff.neofit.NeoResponse;
import org.kllbff.neofit.Neofit;
import org.kllbff.neofit.Neofit.Builder;
import org.kllbff.neofit.annotations.Get;
import org.kllbff.neofit.annotations.Query;
import org.kllbff.neofit.annotations.Service;
import org.kllbff.neofit.exceptions.NeofitException;

public class Test {

    public static void main(String[] args) throws NeofitException, IOException {
        Builder builder = new Builder();
        Neofit neofit = builder.baseUrl("https://jsonplaceholder.typicode.com").addQueryFactory(new QueryFactory(){

            @Override
            public NeoConverter<Object, String> converter(Type parameterType, String queryName) {
                return (Object o) -> {
                    return o.toString();
                };
            }
            
        }).build();
        
        MyService service = neofit.create(MyService.class);
        NeoCall call = service.todos1(1);
        NeoResponse response = call.sync();
        System.out.println(response.code());
        System.out.println(new String(response.getHttpBody().bytes()));
    }
    
    @Service("posts")
    public static interface MyService {
        @Get("1")
        public NeoCall todos1(@Query("id") int id);
    }

}
