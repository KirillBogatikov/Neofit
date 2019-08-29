package org.kllbff.neofit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import org.kllbff.cuba.crutchio.TypeDetector;
import org.kllbff.cuba.crutchio.utils.FileUtils;
import org.kllbff.cuba.reflex.TypeUtils;
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

        @Override
        public NeoConverter<byte[], Object> responseConverter(Type parameterType, String contentType) {
            return null;
        }
        
    }
    
    public static class DefaultFormDataFactory implements FormDataFactory {
        @Override
        public NeoConverter<Object, String> converter(Type parameterType, String itemName) {
            return (object) -> {
                return object.toString();
            };
        }
    }
    
    public static class DefaultPartsFactory implements PartsFactory {
        @Override
        public NeoConverter<Object, Part> converter(Type parameterType, String contentType, String partName) {
            if(parameterType instanceof Class<?>) {
                Class<?> type = (Class<?>)parameterType;
                
                if(type.equals(byte[].class)) {
                    return (object) -> {
                        return Part.create(RequestBody.create(MediaType.parse(contentType), (byte[])object));
                    };
                }
                
                if(TypeUtils.isSubclass(type, CharSequence.class)) {
                    return (object) -> {
                        return Part.create(RequestBody.create(MediaType.parse(contentType), object.toString().getBytes()));
                    };
                }
            }
            
            return null;
        }
    }
}
