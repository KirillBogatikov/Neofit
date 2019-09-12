package org.cuba.neofit.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks service's method as request with custom method<br>
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Request {
    /**
     * Method of this request. Any value can be used
     * @return method of request
     */
    String method();
    
    /**
     * Defines url of linked API method
     * @return url
     */
    String url();
}
