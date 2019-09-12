package org.cuba.neofit.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Links marked class with specified service URL<br>
 * Specified URL will be automatically added to request url<br>
 * after <code>base url</code> and before service's method's url
 *  
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Service {
    /**
     * Specifies service URL: this value can be common url which all<br>
     * class's methods' URLs begin
     * 
     * @return url of service
     */
    String value();
}
