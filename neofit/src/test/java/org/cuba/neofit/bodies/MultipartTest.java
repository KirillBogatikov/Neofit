package org.cuba.neofit.bodies;

import static org.junit.Assert.assertEquals;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.cuba.crutchio.utils.FileUtils;
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
import okio.Buffer;

public class MultipartTest {
    private static MockWebServer server;
    
    @BeforeClass
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {

            private String contentType(Buffer body) throws EOFException {
                String line, contentType = null;
                
                while(!(line = body.readUtf8Line()).trim().isEmpty()) {
                    String[] keyValue = line.split(": ");
                    if(keyValue[0].equals("Content-Type")) {
                        contentType = keyValue[1];
                    }
                }
                
                return contentType;
            }
            
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String url = request.getRequestUrl().toString().replace("http://127.0.0.1/parts/", "");
                
                String boundary = request.getHeader("Content-Type").split("; boundary=")[1];
                MockResponse response = new MockResponse().setHeader("Content-Type", "application/json");
             
                if(url.equals("octet")) {
                    try {
                        Buffer body = request.getBody();
                        String contentType = contentType(body);
                                                
                        if("application/octet-stream".equals(contentType) && body.readUtf8Line().equals("test an octet stream body")) {
                            return response.setBody("success");
                        }
                    } catch (EOFException e) {
                        e.printStackTrace();
                    }
                    
                    return response.setBody("fail");
                }
                if(url.equals("custom")) {
                    try {
                        Buffer body = request.getBody();
                        String contentType = contentType(body);
                                                
                        if("application/custom".equals(contentType) && body.readUtf8Line().equals("test an octet stream body")) {
                            return response.setBody("success");
                        }
                    } catch (EOFException e) {
                        e.printStackTrace();
                    }
                    
                    return response.setBody("fail");
                }
                if(url.equals("png")) {
                    try {
                        Buffer body = request.getBody();
                        String contentType = contentType(body);
                                                
                        if("image/png".equals(contentType)) {
                            return response.setBody("success");
                        }
                    } catch (EOFException e) {
                        e.printStackTrace();
                    }
                    
                    return response.setBody("fail");
                }
                if(url.equals("file")) {
                    try {
                        Buffer body = request.getBody();
                        String contentType = contentType(body);
                                                
                        if("application/zip".equals(contentType)) {
                            return response.setBody("success");
                        }
                    } catch (EOFException e) {
                        e.printStackTrace();
                    }
                    
                    return response.setBody("fail");
                }
                if(url.equals("single")) {
                    String[] parts = request.getBody().readUtf8().split(boundary);
                    if(parts.length == 3 && parts[0].equals("--") && parts[2].trim().equals("--")) {
                        response.setBody("success");
                    } else {
                        response.setBody("fail");
                    }
                    return response;
                }
                                
                if(url.equals("several")) {
                    String[] parts = request.getBody().readUtf8().split(boundary);                    
                    if(parts.length == 4 && parts[0].equals("--") && parts[3].trim().equals("--")) {
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
    
    public MultipartTest() throws NeofitException {
        Neofit neofit = new Neofit.Builder().baseUrl("http://127.0.0.1").build();
        service = neofit.create(TestService.class);
    }

    @Test
    public void testSingle() throws NeofitException, IOException {
        NeoCall call = service.partsSingle("test an octet stream body".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success", response);
    }
    
    @Test
    public void testSeveral() throws NeofitException, IOException {
        NeoCall call = service.partsSeveral("test an octet stream first body item".getBytes(), "test an octet stream second body item".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success", response);
    }

    @Test
    public void testOctetStream() throws NeofitException, IOException {
        NeoCall call = service.partsOctetStream("test an octet stream body".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }

    @Test
    public void testCustom() throws NeofitException, IOException {
        NeoCall call = service.partsCustomType("test an octet stream body".getBytes());
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }

    @Test
    public void testFile() throws NeofitException, IOException, URISyntaxException {
        URI uri = PlainBodyTest.class.getClassLoader().getResource("archive.zip").toURI();
        Path path = Paths.get(uri);
        NeoCall call = service.partsFile(path.toFile());
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }

    @Test
    public void testPng() throws NeofitException, IOException, URISyntaxException {
        URI uri = PlainBodyTest.class.getClassLoader().getResource("image.png").toURI();
        Path path = Paths.get(uri);
        NeoCall call = service.partsBytesPng(FileUtils.readAllBytes(path.toFile()));
        String response = call.sync().to(String.class);
        assertEquals("success",  response);
    }
}
