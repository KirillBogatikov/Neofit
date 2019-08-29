package org.kllbff.neofit.reflection;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.kllbff.neofit.ConverterManager;
import org.kllbff.neofit.NeoCall;
import org.kllbff.neofit.annotations.Service;
import org.kllbff.neofit.exceptions.NeofitException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Part;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ServiceProxy implements InvocationHandler {
    private HttpUrl baseUrl;
    private Map<Method, ServiceMethod> methods;
    private OkHttpClient httpClient;
    private ConverterManager converter;
    
    public ServiceProxy(Class<?> type, HttpUrl baseUrl, OkHttpClient httpClient, ConverterManager converter) throws NeofitException {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.converter = converter;
        
        methods = new HashMap<>();
        
        Service service = type.getAnnotation(Service.class);
        if(service != null) {
            String serviceUrl = service.value();
            if(!serviceUrl.matches(".*[/\\\\]$")) {
                serviceUrl += File.separator;
            }
            this.baseUrl = baseUrl.resolve(serviceUrl);
        }
        
        Method[] methods = type.getDeclaredMethods();
        for(Method method : methods) {
            if(method.isDefault()) {
                continue;
            }
            
            this.methods.put(method, new ServiceMethod(method));
        }
    }

    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {
        if(method.isDefault()) {
            return method.invoke(instance, args);
        }
        
        ServiceMethod serviceMethod = methods.get(method);
        Request.Builder requestBuilder = new Request.Builder();
        
        addHeaders(requestBuilder, serviceMethod.headers(), args);
        requestBuilder.url(compileUrl(serviceMethod.urlPattern(), serviceMethod.pathVariables(), serviceMethod.queries(), args));
        //System.out.println(compileUrl(serviceMethod.urlPattern(), serviceMethod.pathVariables(), serviceMethod.queries(), args));
        
        RequestBody body = null;
        String httpMethod = serviceMethod.httpMethod();
        
        if(serviceMethod.hasFormDataBody()) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for(FormDataItem item : serviceMethod.formDataBody()) {
                String value = converter.parseFormDataItem(item.getParameterType(), item.getName(), args[item.getIndex()]);
                formBodyBuilder.add(item.getName(), value);
            }
            body = formBodyBuilder.build();
        } else if(serviceMethod.hasMultipartBody()) {
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
            for(MultipartItem item : serviceMethod.multiPartBody()) {
                Part part = converter.parseMultipartItem(item.getParameterType(), item.getContentType(), item.getName(), args[item.getIndex()]);
                multipartBuilder.addPart(part);
            }
        } else if(serviceMethod.hasPlainBody()) {
            PlainBody plainBody = serviceMethod.plainBody();
            body = converter.parseRequestBody(plainBody.getClass(), plainBody.getContentType(), args[plainBody.getIndex()]);
        }
                
        requestBuilder.method(httpMethod, body);
        Request request = requestBuilder.build();
        
        return new NeoCall(httpClient.newCall(request), converter);
    }
    
    private void addHeaders(Request.Builder requestBuilder, List<RequestHeader> headers, Object[] args) throws NeofitException {
        for(RequestHeader header : headers) {
            if(header.isConstant()) {
                requestBuilder.addHeader(header.getName(), header.getValue());
                continue;
            }
            
            String value = converter.parseHeader(header.getParameterType(), header.getName(), args[header.getIndex()]);
            requestBuilder.addHeader(header.getName(), value);
        }
    }

    private HttpUrl compileUrl(String url, List<PathVariable> pathVariables, List<UrlQuery> queries, Object[] args) throws NeofitException {
        for(PathVariable path : pathVariables) {
            url = url.replace(String.format("{%s}", path.getVar()), args[path.getIndex()].toString());
        }
        
        StringJoiner queriesJoiner = new StringJoiner("&");
        
        for(UrlQuery query : queries) {
            String queryValue = converter.parseQuery(query.getParameterType(), query.getName(), args[query.getIndex()]);
            queriesJoiner.add(query.getName() + "=" + queryValue);    
        }
        
        if(queriesJoiner.length() != 0) {
            url += "?" + queriesJoiner.toString();
        }
        
        return baseUrl.resolve(url);
    }
}
