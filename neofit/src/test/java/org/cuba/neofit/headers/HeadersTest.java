package org.cuba.neofit.headers;

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

public class HeadersTest {
    private static MockWebServer server;
    
    @BeforeClass
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {

            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String url = request.getRequestUrl().toString().replace("http://127.0.0.1/headers/", "");
                                
                MockResponse response = new MockResponse().setHeader("Content-Type", "application/json");
             
                if(url.equals("one")) {
                    if(request.getHeader("Test-Header").equals("header-value")) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                if(url.equals("two")) {
                    if(request.getHeader("Test-First-Header").equals("header-value-1") &&
                       request.getHeader("Test-Second-Header").equals("header-value-2")) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                if(url.equals("several")) {
                    if(request.getHeader("Test-First-Header").equals("first") &&
                       request.getHeader("Test-Second-Header").equals("second")) {
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

    public HeadersTest() throws NeofitException {
        Neofit neofit = new Neofit.Builder().baseUrl("http://127.0.0.1").build();
        service = neofit.create(TestService.class);
    }

    @Test
    public void testOne() throws NeofitException, IOException {
        NeoCall call = service.one("header-value");
        String response = call.sync().to(String.class);
        assertEquals("success", response);
    }

    @Test
    public void testTwo() throws NeofitException, IOException {
        NeoCall call = service.two("header-value-1", "header-value-2");
        String response = call.sync().to(String.class);
        assertEquals("success", response);
    }

    @Test
    public void testSeveral() throws NeofitException, IOException {
        NeoCall call = service.several();
        String response = call.sync().to(String.class);
        assertEquals("success", response);
    }
}
