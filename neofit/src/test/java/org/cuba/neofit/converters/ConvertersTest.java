package org.cuba.neofit.converters;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
import okio.Buffer;

public class ConvertersTest {
    private static MockWebServer server;
    
    @BeforeClass
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {

            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String url = request.getRequestUrl().toString().replace("http://127.0.0.1/", "");
                                
                MockResponse response = new MockResponse().setHeader("Content-Type", "application/json");
             
                if(url.equals("string")) {
                    return response.setBody("success");
                }
                if(url.equals("byte")) {
                    return response.setBody("-127");
                }
                if(url.equals("short")) {
                    return response.setBody("32760");
                }
                if(url.equals("integer")) {
                    return response.setBody("-2147483640");
                }
                if(url.equals("long")) {
                    return response.setBody(Long.toString(9223372036854775000L));
                }
                if(url.equals("float")) {
                    return response.setBody(Float.toString(3.4e+037f));
                }
                if(url.equals("double")) {
                    return response.setBody(Double.toString(1.7e+307));
                }
                if(url.equals("boolean")) {
                    return response.setBody("false");
                }
                if(url.equals("char")) {
                    return response.setBody("!");
                }
                
                if(url.equals("octal")) {
                    return response.setBody("017");
                }
                if(url.equals("hexademical")) {
                    return response.setBody("0xFF");
                }
                if(url.equals("binary")) {
                    return response.setBody("-0b011010111");
                }
                if(url.equals("serializable")) {
                    try(Buffer buffer = new Buffer();
                        ObjectOutputStream objectStream = new ObjectOutputStream(buffer.outputStream())) {
                        objectStream.writeObject(new SerializableItem(100246, "objectName"));
                        return response.setBody(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
    
    public ConvertersTest() throws NeofitException {
        Neofit neofit = new Neofit.Builder().baseUrl("http://127.0.0.1").build();
        service = neofit.create(TestService.class);
    }
    
    @Test
    public void testString() throws NeofitException, IOException {
        NeoCall call = service.stringValue();
        String result = call.sync().to(String.class);
        assertEquals("success", result);
    }
    
    @Test
    public void testByte() throws NeofitException, IOException {
        NeoCall call = service.byteValue();
        byte result = call.sync().to(byte.class);
        assertEquals(-127, result);
    }
    
    @Test
    public void testShort() throws NeofitException, IOException {
        NeoCall call = service.shortValue();
        short result = call.sync().to(short.class);
        assertEquals(32760, result);
    }
    
    @Test
    public void testInteger() throws NeofitException, IOException {
        NeoCall call = service.integerValue();
        int result = call.sync().to(Integer.class);
        assertEquals(-2147483640, result);
    }
    
    @Test
    public void testLong() throws NeofitException, IOException {
        NeoCall call = service.longValue();
        long result = call.sync().to(long.class);
        assertEquals(9223372036854775000L, result);
    }
    
    @Test
    public void testFloat() throws NeofitException, IOException {
        NeoCall call = service.floatValue();
        float result = call.sync().to(Float.class);
        assertEquals(3.4e+037f, result, 1f);
    }
    
    @Test
    public void testDouble() throws NeofitException, IOException {
        NeoCall call = service.doubleValue();
        double result = call.sync().to(double.class);
        assertEquals(1.7e+307, result, 0.5);
    }
    
    @Test
    public void testBoolean() throws NeofitException, IOException {
        NeoCall call = service.booleanValue();
        boolean result = call.sync().to(boolean.class);
        assertEquals(false, result);
    }
    
    @Test
    public void testChar() throws NeofitException, IOException {
        NeoCall call = service.charValue();
        char result = call.sync().to(Character.class);
        assertEquals('!', result);
    }
    
    @Test
    public void testOctal() throws NeofitException, IOException {
        NeoCall call = service.octal();
        int result = call.sync().to(Integer.class);
        assertEquals(15, result);
    }
    
    @Test
    public void testHexademical() throws NeofitException, IOException {
        NeoCall call = service.hex();
        int result = call.sync().to(Integer.class);
        assertEquals(255, result);
    }
    
    @Test
    public void testBinary() throws NeofitException, IOException {
        NeoCall call = service.bin();
        int result = call.sync().to(Integer.class);
        assertEquals(-215, result);
    }
    
    @Test
    public void testSerializable() throws NeofitException, IOException {
        NeoCall call = service.serializableValue();
        SerializableItem result = call.sync().to(SerializableItem.class);
        assertEquals(new SerializableItem(100246, "objectName"), result);
    }
}
