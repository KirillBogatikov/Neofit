package org.cuba.neofit.bodies;

import static org.junit.Assert.*;

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

public class FormBodyTest {
    private static MockWebServer server;
    
    @BeforeClass
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {

            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String url = request.getRequestUrl().toString().replace("http://127.0.0.1/form/", "");
                                
                MockResponse response = new MockResponse().setHeader("Content-Type", "application/json");
             
                if(url.equals("single")) {
                    if(request.getBody().readUtf8().split("=").length == 2) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                
                if(url.equals("several")) {
                    if(request.getBody().readUtf8().split("&").length == 2) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                
                return response;
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
    
    public FormBodyTest() throws NeofitException {
        Neofit neofit = new Neofit.Builder().baseUrl("http://127.0.0.1").build();
        service = neofit.create(TestService.class);
    }
    
    @Test
    public void testSingle() throws NeofitException, IOException {
        NeoCall call = service.formSingle("test an octet stream body".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success", response);
    }
    
    @Test
    public void testSeveral() throws NeofitException, IOException {
        NeoCall call = service.formSeveral("test an octet stream first body item".getBytes(), "test an octet stream second body item".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success", response);
    }
}
