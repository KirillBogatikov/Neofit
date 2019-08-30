package org.kllbff.neofit.bodies;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kllbff.cuba.crutchio.utils.FileUtils;
import org.kllbff.neofit.NeoCall;
import org.kllbff.neofit.Neofit;
import org.kllbff.neofit.exceptions.NeofitException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

public class PlainBodyTest {
    private static MockWebServer server;
    
    @BeforeClass
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {

            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String url = request.getRequestUrl().toString().replace("http://127.0.0.1/plain/", "");
                                
                MockResponse response = new MockResponse().setHeader("Content-Type", "application/json");
             
                if(url.equals("octet")) {
                    if(request.getHeader("Content-Type").equals("application/octet-stream") && request.getBody().readUtf8().equals("test an octet stream body")) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                if(url.equals("custom")) {
                    if(request.getHeader("Content-Type").equals("application/custom")) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                if(url.equals("png")) {
                    if(request.getHeader("Content-Type").equals("image/png")) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                if(url.equals("file")) {
                    if(request.getHeader("Content-Type").equals("application/zip")) {
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
    
    public PlainBodyTest() throws NeofitException {
        Neofit neofit = new Neofit.Builder().baseUrl("http://127.0.0.1").build();
        service = neofit.create(TestService.class);
    }

    @Test
    public void testOctetStream() throws NeofitException, IOException {
        NeoCall call = service.plainOctetStream("test an octet stream body".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }

    @Test
    public void testCustom() throws NeofitException, IOException {
        NeoCall call = service.plainCustomType("test an octet stream body".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }

    @Test
    public void testFile() throws NeofitException, IOException, URISyntaxException {
        URI uri = PlainBodyTest.class.getClassLoader().getResource("archive.zip").toURI();
        Path path = Paths.get(uri);
        NeoCall call = service.plainFile(path.toFile());
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }

    @Test
    public void testPng() throws NeofitException, IOException, URISyntaxException {
        URI uri = PlainBodyTest.class.getClassLoader().getResource("image.png").toURI();
        Path path = Paths.get(uri);
        NeoCall call = service.plainPngBytes(FileUtils.readAllBytes(path.toFile()));
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }
}
