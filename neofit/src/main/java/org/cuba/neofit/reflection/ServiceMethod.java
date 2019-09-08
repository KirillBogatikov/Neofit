package org.cuba.neofit.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.cuba.neofit.annotations.Body;
import org.cuba.neofit.annotations.Delete;
import org.cuba.neofit.annotations.FormItem;
import org.cuba.neofit.annotations.Get;
import org.cuba.neofit.annotations.Head;
import org.cuba.neofit.annotations.Header;
import org.cuba.neofit.annotations.Headers;
import org.cuba.neofit.annotations.Part;
import org.cuba.neofit.annotations.Path;
import org.cuba.neofit.annotations.Post;
import org.cuba.neofit.annotations.Put;
import org.cuba.neofit.annotations.Query;
import org.cuba.neofit.annotations.Request;
import org.cuba.neofit.exceptions.NeofitException;

public class ServiceMethod {
    private Method method;
    private String httpMethod;
    private String urlPattern;
    private List<RequestHeader> headers;
    private List<UrlQuery> queries;
    private List<PathVariable> pathVariables;
    private PlainBody body;
    private List<FormDataItem> formItems;
    private List<MultipartItem> parts;
    
    public ServiceMethod(Method method) throws NeofitException {
        this.method = method;
        parseRequestAnnotation();
        parseHeaders();
        parseParameters();
    }
    
    public boolean hasPlainBody() {
        return body != null;
    }

    public boolean hasFormDataBody() {
        return !formItems.isEmpty();
    }
    
    public boolean hasMultipartBody() {
        return !parts.isEmpty();
    }
    
    private void parseRequestAnnotation() throws NeofitException {
        Get get = method.getAnnotation(Get.class);
        if(get != null) {
            httpMethod = "GET";
            urlPattern = get.value();
            return;
        }
        
        Post post = method.getAnnotation(Post.class);
        if(post != null) {
            httpMethod = "POST";
            urlPattern = post.value();
            return;
        }
        
        Put put = method.getAnnotation(Put.class);
        if(put != null) {
            httpMethod = "PUT";
            urlPattern = put.value();
            return;
        }
        
        Head head = method.getAnnotation(Head.class);
        if(head != null) {
            httpMethod = "HEAD";
            urlPattern = head.value();
            return;
        }
        
        Delete delete = method.getAnnotation(Delete.class);
        if(delete != null) {
            httpMethod = "DELETE";
            urlPattern = delete.value();
            return;
        }
        
        Request request = method.getAnnotation(Request.class);
        if(request != null) {
            httpMethod = request.method();
            urlPattern = request.url();
            return;
        }
        
        throw new NeofitException("Service's method shoul be annotated by one of the annotations: Get, Post, Put, Delete, Head or Request");
    }
    
    private void parseHeaders() {
        headers = new ArrayList<>();
        
        Headers headers = method.getAnnotation(Headers.class);
        if(headers != null) {
            for(String header : headers.value()) {
                String[] values = header.split(":");
                this.headers.add(new RequestHeader(values[0].trim(), values[1].trim()));
            }
        }
    }
    
    private void parseParameters() throws NeofitException {
        queries = new ArrayList<>();
        pathVariables = new ArrayList<>();
        formItems = new ArrayList<>();
        parts = new ArrayList<>();
        
        Type[] paramTypes = method.getGenericParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for(int i = 0; i < paramTypes.length; i++) {
            for(Annotation annotation : paramAnnotations[i]) {
                
                if(annotation instanceof Header) {
                    Header header = (Header)annotation;
                    headers.add(new RequestHeader(header.value(), paramTypes[i], i));
                    continue;
                }
            
                if(annotation instanceof Body) {
                    Body body = (Body)annotation;
                    if(this.body != null) {
                        throw new NeofitException("Request can has only one body!");
                    }
                    if(hasMultipartBody() || hasFormDataBody()) {
                        throw new NeofitException("Request can not has Multipart body or FormData items and plain body together!");
                    }
                    if(httpMethod.equalsIgnoreCase("GET") || httpMethod.equalsIgnoreCase("HEAD")) {
                        throw new NeofitException("GET and HEAD requests can not has any body");
                    }
                    
                    this.body = new PlainBody(body.contentType(), paramTypes[i], i);
                    continue;
                }
                
                if(annotation instanceof Part) {
                    Part part = (Part)annotation;
                    if(this.body != null) {
                        throw new NeofitException("Request can not has Multipart body and plain body together!");
                    }
                    if(!formItems.isEmpty()) {
                        throw new NeofitException("Request can not has Multipart body and FormData body together!");
                    }
                    if(httpMethod.equalsIgnoreCase("GET") || httpMethod.equalsIgnoreCase("HEAD")) {
                        throw new NeofitException("GET and HEAD requests can not has any body");
                    }
                    
                    this.parts.add(new MultipartItem(part.value(), part.contentType(), paramTypes[i], i));
                    continue;
                }
                
                if(annotation instanceof FormItem) {
                    FormItem formItem = (FormItem)annotation;
                    if(this.body != null) {
                        throw new NeofitException("Request can not has FormData body and plain body together!");
                    }
                    if(!parts.isEmpty()) {
                        throw new NeofitException("Request can not has FormData body and Multipart body together!");
                    }
                    if(httpMethod.equalsIgnoreCase("GET") || httpMethod.equalsIgnoreCase("HEAD")) {
                        throw new NeofitException("GET and HEAD requests can not has any body");
                    }
                    
                    this.formItems.add(new FormDataItem(formItem.value(), paramTypes[i], i));
                    continue;
                }
                
                if(annotation instanceof Query) {
                    Query query = (Query)annotation;
                    this.queries.add(new UrlQuery(query.value(), paramTypes[i], i));
                    continue;
                }
                
                if(annotation instanceof Path) {
                    Path path = (Path)annotation;
                    this.pathVariables.add(new PathVariable(path.value(), i));
                }
            }
        }
    }
    
    public String urlPattern() {
        return urlPattern;
    }
    
    public List<PathVariable> pathVariables() {
        return pathVariables;
    }
    
    public List<RequestHeader> headers() {
        return headers;
    }
    
    public List<UrlQuery> queries() {
        return queries;
    }
    
    public List<MultipartItem> multiPartBody() {
        return parts;
    }

    public List<FormDataItem> formDataBody() {
        return formItems;
    }

    public PlainBody plainBody() {
        return body;
    }
    
    public String httpMethod() {
        return httpMethod;
    }

}
