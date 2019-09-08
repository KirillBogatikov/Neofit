package org.cuba.neofit.url;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;

import org.cuba.neofit.NeoCall;
import org.cuba.neofit.Neofit;
import org.cuba.neofit.exceptions.NeofitException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

public class UrlBuildingTest {
    private static MockWebServer server;
    
    @BeforeClass
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {

            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String url = request.getRequestUrl().toString().replace("http://127.0.0.1/", "");
                String method = request.getMethod();
                
                MockResponse response = new MockResponse().setHeader("Content-Type", "application/json");
                
                System.out.println(method);
                System.out.println(url);
                
                if(method.equalsIgnoreCase("GET")) {
                    if(url.equals("dir/sub0/sub1/sub2")) {
                        return response.setBody("success");
                    } else {
                        return response.setBody("fail");
                    }
                }
                
                if(method.equalsIgnoreCase("POST")) {
                    if(url.equals("dir/sub0/?query1=a&query2=b")) {
                        return response.setBody("success");
                    } else {
                        return response.setBody("fail");
                    }
                }
                
                if(method.equalsIgnoreCase("complex")) {
                    if(url.equals("dir/sub0/sub1/sub2/sub3/sub4/sub5?query1=q51&query2=t72")) {
                        return response.setBody("success");
                    } else {
                        return response.setBody("fail");
                    }
                }
                
                return null;
            }

            public MockResponse peek() {
                return new MockResponse().setSocketPolicy(SocketPolicy.KEEP_OPEN);
            }
            
        });
        server.start(InetAddress.getByName("127.0.0.1"), 80);
    }
    
    @AfterClass
    public static void release() throws Exception {
        server.close();
    }
    
    private TestService service;
    
    public UrlBuildingTest() throws NeofitException {
        Neofit neofit = new Neofit.Builder().baseUrl("http://127.0.0.1").build();
        service = neofit.create(TestService.class);
    }

    @Test
    public void testPathVariables() throws NeofitException, IOException {
        NeoCall call = service.path("sub1", "sub2");
        String message = call.sync().to(String.class);
        assertEquals("success", message);
    }

    @Test
    public void testQueries() throws NeofitException, IOException {
        NeoCall call = service.queries("a", "b");
        String message = call.sync().to(String.class);
        assertEquals("success", message);
    }

    @Test
    public void testComplex() throws NeofitException, IOException {
        NeoCall call = service.complex("sub3", "sub5", "q51", "t72");
        String message = call.sync().to(String.class);
        assertEquals("success", message);
    }
}
