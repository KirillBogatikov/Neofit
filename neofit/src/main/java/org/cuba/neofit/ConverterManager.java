package org.cuba.neofit;

import java.lang.reflect.Type;
import java.util.List;

import org.cuba.neofit.NeoConverter.BodyFactory;
import org.cuba.neofit.NeoConverter.FormDataFactory;
import org.cuba.neofit.NeoConverter.HeaderFactory;
import org.cuba.neofit.NeoConverter.PartsFactory;
import org.cuba.neofit.NeoConverter.QueryFactory;
import org.cuba.neofit.exceptions.NeofitException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ConverterManager {
    private List<QueryFactory> queryFactories;
    private List<HeaderFactory> headerFactories;
    private List<BodyFactory> bodyFactories;
    private List<FormDataFactory> formFactories;
    private List<PartsFactory> partsFactories;
    
    public ConverterManager(List<QueryFactory> queryFactories, List<HeaderFactory> headerFactories, List<BodyFactory> bodyFactories,
                            List<FormDataFactory> formFactories, List<PartsFactory> partsFactories) {
        this.queryFactories = queryFactories;
        this.headerFactories = headerFactories;
        this.bodyFactories = bodyFactories;
        this.formFactories = formFactories;
        this.partsFactories = partsFactories;
    }
        
    public String parseQuery(Type parameterType, String queryName, Object source) throws NeofitException {
        boolean hasConverter = false;
        
        for(QueryFactory factory : queryFactories) {
            NeoConverter<Object, String> converter = factory.converter(parameterType, queryName);
            if(converter == null) {
                continue;
            }
            
            hasConverter = true;
            
            String result = converter.convert(source);
            if(result != null) {
                return result;
            }
        }
        
        if(hasConverter) {
            throw new NeofitException("No converter could convert the value \"" + source + "\" of type " + parameterType);
        }
        throw new NeofitException("No converter found for type " + parameterType);
    }
    
    public String parseHeader(Type parameterType, String headerName, Object source) throws NeofitException {
        boolean hasConverter = false;
        
        for(HeaderFactory factory : headerFactories) {
            NeoConverter<Object, String> converter = factory.converter(parameterType, headerName);
            if(converter == null) {
                continue;
            }
            
            hasConverter = true;
            
            String result = converter.convert(source);
            if(result != null) {
                return result;
            }
        }
        
        if(hasConverter) {
            throw new NeofitException("No converter could convert the value \"" + source + "\" of type " + parameterType);
        }
        throw new NeofitException("No converter found for type " + parameterType);
    }
    
    public String parseFormDataItem(Type parameterType, String itemName, Object source) throws NeofitException {
        boolean hasConverter = false;
        
        for(FormDataFactory factory : formFactories) {
            NeoConverter<Object, String> converter = factory.converter(parameterType, itemName);
            if(converter == null) {
                continue;
            }
            
            hasConverter = true;
            
            String result = converter.convert(source);
            if(result != null) {
                return result;
            }
        }
        
        if(hasConverter) {
            throw new NeofitException("No converter could convert the value \"" + source + "\" of type " + parameterType);
        }
        throw new NeofitException("No converter found for type " + parameterType);
    }

    public MultipartBody.Part parseMultipartItem(Type parameterType, String contentType, String itemName, Object source) throws NeofitException {
        boolean hasConverter = false;
        
        for(PartsFactory factory : partsFactories) {
            NeoConverter<Object, MultipartBody.Part> converter = factory.converter(parameterType, contentType, itemName);
            if(converter == null) {
                continue;
            }
            
            hasConverter = true;
            
            MultipartBody.Part result = converter.convert(source);
            if(result != null) {
                return result;
            }
        }

        if(hasConverter) {
            throw new NeofitException("No converter could convert the value \"" + source + "\" of type " + parameterType);
        }
        throw new NeofitException("No converter found for type " + parameterType);
    }

    public RequestBody parseRequestBody(Type parameterType, String contentType, Object source) throws NeofitException {
        boolean hasConverter = false;
        
        for(BodyFactory factory : bodyFactories) {
            NeoConverter<Object, RequestBody> converter = factory.requestConverter(parameterType, contentType);
            if(converter == null) {
                continue;
            }
            
            hasConverter = true;
            
            RequestBody result = converter.convert(source);
            if(result != null) {
                return result;
            }
        }
        
        if(hasConverter) {
            throw new NeofitException("No converter could convert the value \"" + source + "\" of type " + parameterType);
        }
        throw new NeofitException("No converter found for type " + parameterType);
    }

    public Object parseResponseBody(Type parameterType, String contentType, byte[] source) throws NeofitException {
        boolean hasConverter = false;
        
        for(BodyFactory factory : bodyFactories) {
            NeoConverter<byte[], Object> converter = factory.responseConverter(parameterType, contentType);
            if(converter == null) {
                continue;
            }
            
            hasConverter = true;
            
            Object result = converter.convert(source);
            if(result != null) {
                return result;
            }
        }
        
        if(hasConverter) {
            throw new NeofitException("No converter could convert the value \"" + source + "\" of type " + parameterType);
        }
        throw new NeofitException("No converter found for type " + parameterType);
    }
}
