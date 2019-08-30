package org.kllbff.neofit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.kllbff.cuba.crutchio.TypeDetector;
import org.kllbff.cuba.crutchio.utils.FileUtils;
import org.kllbff.cuba.reflex.TypeUtils;
import org.kllbff.cuba.reflex.ValueUtils;
import org.kllbff.neofit.NeoConverter.BodyFactory;
import org.kllbff.neofit.NeoConverter.FormDataFactory;
import org.kllbff.neofit.NeoConverter.HeaderFactory;
import org.kllbff.neofit.NeoConverter.PartsFactory;
import org.kllbff.neofit.NeoConverter.QueryFactory;

import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;

public class DefaultConverter {
    public static class DefaultQueryFactory implements QueryFactory {
        @Override
        public NeoConverter<Object, String> converter(Type parameterType, String queryName) {
            return (object) -> {
                return object.toString();
            };
        }
    }
    
    public static class DefaultHeaderFactory implements HeaderFactory {
        @Override
        public NeoConverter<Object, String> converter(Type parameterType, String headerName) {
            return (object) -> {
                return object.toString();
            };
        }
    }
    
    public static class DefaultBodyFactory implements BodyFactory {

        @Override
        public NeoConverter<Object, RequestBody> requestConverter(Type parameterType, String contentType) {
            if(parameterType instanceof Class<?>) {
                Class<?> type = (Class<?>)parameterType;
                
                if(TypeUtils.isSubclass(type, CharSequence.class)) {
                    return (object) -> {
                        CharSequence sequence = (CharSequence)object;
                        MediaType mediaType;
                        if(contentType.isEmpty()) {
                            mediaType = MediaType.get("text/plain");
                        } else {
                            mediaType = MediaType.parse(contentType);
                        }
                        
                        return RequestBody.create(mediaType, sequence.toString().getBytes());
                    };
                }
                
                if(TypeUtils.isSubclass(type, Serializable.class)) {
                    return (object) -> {
                        try(ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
                            objectStream.writeObject(object);
                            return RequestBody.create(MediaType.get("application/octet-stream"), byteStream.toByteArray());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }  
                    };
                }
                
                if(type.equals(byte[].class) || type.equals(Byte[].class)) {
                    return (object) -> {
                        if(type.equals(Byte[].class)) {
                            Byte[] origin = (Byte[])object;
                            byte[] array = new byte[origin.length];
                            System.arraycopy(origin, 0, array, 0, origin.length);
                            object = array;
                        }
                        
                        MediaType mediaType = null;
                        if(contentType.isEmpty()) {
                            TypeDetector detector = new TypeDetector();
                            String mimeType = detector.mimeType((byte[])object);
                            if(mimeType != null) {
                                mediaType = MediaType.parse(mimeType);
                            }
                        } else {
                            mediaType = MediaType.parse(contentType);
                        } 
                        
                        if(mediaType == null) {
                            mediaType = MediaType.get("application/octet-stream");
                        }
                        
                        
                        return RequestBody.create(mediaType, (byte[])object);
                    };
                }
                
                if(TypeUtils.isSubclass(type, File.class)) {
                    return (object) -> {
                        File file = (File)object;
                        try {
                            byte[] bytes = FileUtils.readAllBytes(file);
                            
                            MediaType mediaType;
                            if(contentType.isEmpty()) {
                                TypeDetector typeDetector = new TypeDetector();
                                mediaType = MediaType.get(typeDetector.mimeType(bytes));
                            } else {
                                mediaType = MediaType.parse(contentType);
                            }
                            
                            return RequestBody.create(mediaType, bytes);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    };
                }                
            }

            return null;
        }
                
        private Number parseNumber(byte[] bytes) {
            String s = new String(bytes);
            if(s.matches("(-)?0b[0-1]+")) {
                return Long.valueOf(s.replace("0b", ""), 2);
            }
            if(s.matches("(-)?0x[0-9A-Fa-f]+")) {
                return Long.valueOf(s.replace("0x", ""), 16);
            }
            if(s.matches("(-)?0[0-7]+")) {
                return Long.valueOf(s.replaceFirst("0", ""), 8);
            }
            return Double.valueOf(s);
        }

        @Override
        public NeoConverter<byte[], Object> responseConverter(Type parameterType, String contentType) {
            if(parameterType instanceof Class) {
                Class<?> type = (Class<?>)parameterType;
                if(type.isPrimitive()) {
                    type = ValueUtils.boxTypeFor(type);
                }

                if(type.equals(Byte.class)) {
                    return bytes -> parseNumber(bytes).byteValue();
                }
                if(type.equals(Short.class)) {
                    return bytes -> parseNumber(bytes).shortValue();
                }
                if(type.equals(Integer.class)) {
                    return bytes -> parseNumber(bytes).intValue();
                }
                if(type.equals(Long.class)) {
                    return bytes -> { 
                        Number num = parseNumber(bytes);
                        if(num instanceof Long) {
                            return num;
                        }
                        return Long.valueOf(new String(bytes));
                    };
                }
                if(type.equals(Float.class)) {
                    return bytes -> Float.valueOf(new String(bytes));
                }
                if(type.equals(Double.class)) {
                    return bytes -> Double.valueOf(new String(bytes));
                }
                if(type.equals(Boolean.class)) {
                    return bytes -> Boolean.valueOf(new String(bytes));
                }
                if(type.equals(Character.class)) {
                    return bytes -> new String(bytes).charAt(0);
                }
                if(TypeUtils.isSubclass(type, String.class)) {
                    return bytes -> new String(bytes);
                }
            }
            
            return null;
        }
        
    }
    
    public static class DefaultFormDataFactory implements FormDataFactory {
        @Override
        public NeoConverter<Object, String> converter(Type parameterType, String itemName) {
            if(parameterType instanceof Class) {
                Class<?> type = (Class<?>)parameterType;
                                
                if(type.isArray()) {
                    Class<?> component = type.getComponentType();
                    
                    if(component.isPrimitive()) {
                        if(component.equals(byte.class)) {
                            return object -> Arrays.toString((byte[])object);
                        }
                        if(component.equals(short.class)) {
                            return object -> Arrays.toString((short[])object);
                        }
                        if(component.equals(int.class)) {
                            return object -> Arrays.toString((int[])object);
                        }
                        if(component.equals(long.class)) {
                            return object -> Arrays.toString((long[])object);
                        }
                        if(component.equals(float.class)) {
                            return object -> Arrays.toString((float[])object);
                        }
                        if(component.equals(double.class)) {
                            return object -> Arrays.toString((double[])object);
                        }
                        if(component.equals(boolean.class)) {
                            return object -> Arrays.toString((boolean[])object);
                        }
                        if(component.equals(char.class)) {
                            return object -> Arrays.toString((char[])object);
                        }
                    }
                    
                    return object -> Arrays.toString((Object[])object);
                }
            }
            
            return object -> object.toString();
        }
    }
    
    public static class DefaultPartsFactory implements PartsFactory {
        @Override
        public NeoConverter<Object, Part> converter(Type parameterType, String contentType, String partName) {
            if(parameterType instanceof Class<?>) {
                Class<?> type = (Class<?>)parameterType;
                
                if(type.equals(byte[].class)) {
                    return (object) -> {
                        RequestBody body = RequestBody.create(MediaType.parse(contentType), (byte[])object);
                        return Part.create(body);
                    };
                }
                
                if(TypeUtils.isSubclass(type, CharSequence.class)) {
                    return (object) -> {
                        return Part.create(RequestBody.create(MediaType.parse(contentType), object.toString().getBytes()));
                    };
                }
                
                if(TypeUtils.isSubclass(type, File.class)) {
                    return (object) -> {
                        File file = (File)object;
                        try {
                            byte[] bytes = FileUtils.readAllBytes(file);
                            
                            MediaType mediaType;
                            if(contentType.isEmpty()) {
                                TypeDetector typeDetector = new TypeDetector();
                                String mimeType = typeDetector.mimeType(bytes);
                                mediaType = MediaType.get(mimeType == null ? "application/octet-stream" : mimeType);
                            } else {
                                mediaType = MediaType.parse(contentType);
                            }
                            
                            return Part.createFormData(partName, "", RequestBody.create(mediaType, bytes));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    };
                }
            }
            
            return null;
        }
    }
}
