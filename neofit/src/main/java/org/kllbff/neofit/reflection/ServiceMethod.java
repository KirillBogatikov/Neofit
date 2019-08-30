package org.kllbff.neofit.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.kllbff.neofit.annotations.Body;
import org.kllbff.neofit.annotations.Delete;
import org.kllbff.neofit.annotations.FormItem;
import org.kllbff.neofit.annotations.Get;
import org.kllbff.neofit.annotations.Head;
import org.kllbff.neofit.annotations.Header;
import org.kllbff.neofit.annotations.Headers;
import org.kllbff.neofit.annotations.Part;
import org.kllbff.neofit.annotations.Path;
import org.kllbff.neofit.annotations.Post;
import org.kllbff.neofit.annotations.Put;
import org.kllbff.neofit.annotations.Query;
import org.kllbff.neofit.annotations.Request;
import org.kllbff.neofit.exceptions.NeofitException;

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
        
        Parameter[] parameters = method.getParameters();
        for(int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            
            Header header = param.getAnnotation(Header.class);
            if(header != null) {
                headers.add(new RequestHeader(header.value(), param.getParameterizedType(), i));
                continue;
            }
            
            Body body = param.getAnnotation(Body.class);
            if(body != null) {
                if(this.body != null) {
                    throw new NeofitException("Request can has only one body!");
                }
                if(hasMultipartBody() || hasFormDataBody()) {
                    throw new NeofitException("Request can not has Multipart body or FormData items and plain body together!");
                }
                if(httpMethod.equalsIgnoreCase("GET") || httpMethod.equalsIgnoreCase("HEAD")) {
                    throw new NeofitException("GET and HEAD requests can not has any body");
                }
                
                this.body = new PlainBody(body.contentType(), param.getParameterizedType(), i);
                continue;
            }
            
            Part part = param.getAnnotation(Part.class);
            if(part != null) {
                if(this.body != null) {
                    throw new NeofitException("Request can not has Multipart body and plain body together!");
                }
                if(!formItems.isEmpty()) {
                    throw new NeofitException("Request can not has Multipart body and FormData body together!");
                }
                if(httpMethod.equalsIgnoreCase("GET") || httpMethod.equalsIgnoreCase("HEAD")) {
                    throw new NeofitException("GET and HEAD requests can not has any body");
                }
                
                this.parts.add(new MultipartItem(part.value(), part.contentType(), param.getParameterizedType(), i));
                continue;
            }
            
            FormItem formItem = param.getAnnotation(FormItem.class);
            if(formItem != null) {
                if(this.body != null) {
                    throw new NeofitException("Request can not has FormData body and plain body together!");
                }
                if(!parts.isEmpty()) {
                    throw new NeofitException("Request can not has FormData body and Multipart body together!");
                }
                if(httpMethod.equalsIgnoreCase("GET") || httpMethod.equalsIgnoreCase("HEAD")) {
                    throw new NeofitException("GET and HEAD requests can not has any body");
                }
                
                this.formItems.add(new FormDataItem(formItem.value(), param.getParameterizedType(), i));
                continue;
            }
            
            Query query = param.getAnnotation(Query.class);
            if(query != null) {
                this.queries.add(new UrlQuery(query.value(), param.getParameterizedType(), i));
                continue;
            }
            
            Path path = param.getAnnotation(Path.class);
            if(path != null) {
                this.pathVariables.add(new PathVariable(path.value(), i));
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
