package org.cuba.neofit.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks method's parameter as request's body<br>
 * Can be only one in method
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Body {
    /**
     * Overrides value of <i>Content-Type</i> header<br>
     * By default, Neofit determines contentType of body automatically<br>
     * or uses <code>application/octet-stream</code> if type is unknown
     * @return content type
     */
    String contentType() default "";
}
