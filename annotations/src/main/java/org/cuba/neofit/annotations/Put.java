package org.cuba.neofit.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks service's method as request with method <code>PUT</code><br>
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Put {
    /**
     * Defines url of linked API method
     * @return url
     */
    String value();
}
