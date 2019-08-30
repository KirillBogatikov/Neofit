# NEOFIT
#### Part of CUBA project
---
Neofit is a library that allows you to quickly and easily develop the client part of the RESTful API service. For the preparation and execution of HTTP requests using an HTTP client OkHttp3. For ease of use the project is divided into several modules:
 - Annotations  
    > Annotations module contains only declarative annotations for marking your interfaces. Neofit supports come basic HTTP-methods available with prebuilt annotations: @Get, @Post, @Put, @Delete. Despite such a modest arsenal of built-in methods, the Neofit is famous for its extensibility. By using the @Request annotation, you can use a nonstandard method by specifying it as a method parameter.  
    > The module also contains annotations to describe the requests themselves. @Service defines the base URL for all methods within the interface. This is an optional annotation, but it will make Your life much easier if you need to refactor requests addresses. You can also use variables in the requests URL address, such as </user/{id}>. The value of such a variable is determined by one of the method parameters marked with the @Path annotation. @Query annotation allows you to describe the query parameter passed in its URL after the character <?>.     
    > @Header and @Headers annotations allows to define custom request headers. @Headers provides a sequence of constant headers, but by @Header can be annotated method parameter. It provides header with variable value.  
    > @Body, @FormItem and @Part designed to describe the body of the request. You can use @Body to describe a normal query body. @FormItem is used to represent text data as key:value pairs within the query body. @Part is a component of multipart body and is used to transfer a variety of data in the form of body segments. Each part has its own name, content type and, directly, byte array content.
 - Gson
    > Gson module contains implementations of few Factories for converting Body (Response and Request), Part and Queries, which contains Json data. Also, by using this module you can convert response into POJO or POJO into request body.    
 - Android
    > Neofit uses implementations of abstract class Platform for obtaining executors: statement executor and callback executor. Android platform allows to execute statements in ThreadPoolm and Callback in System UI thread
 - Neofit
    > Neofit is main module, which contains main functional and tests for it.
    
---
    
Neofit is extremely extensible and customizable. You can manually control the content type of requests, or you can entrust this work to Type Detector for auto-detection. You can pass custom headers, including variable headers, and use custom query types. Neofit offers a wide range of factories for converting data: implementarea them, You can override the algorithms for serialization and deserialization of the request body, including its parts (MultipartBody), the data pairs of the form (FormDataBody) and even query parameters.  
All this is already available here and now... By NEOFIT!

---

### Using Neofit  
You can add dependency for any module:  
- Main module  
    > &lt;dependency&gt;
          &lt;groupId&gt;org.kllbff.cuba.neofit&lt;/groupId&gt;
          &lt;artifactId&gt;neofit&lt;/artifactId&gt;
          &lt;version&gt;1.0.0&lt;/version&gt;
      &lt;/dependency&gt;
    