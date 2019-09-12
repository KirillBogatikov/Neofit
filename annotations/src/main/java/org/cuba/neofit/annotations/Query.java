package org.cuba.neofit.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks method's parameter as URL query item<br>
 * All queries will be added to request URL at building<br>
 * call
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Query {
    /**
     * Specifies query item name. Queries added after<br>
     * request url and <code>?</code> symbol. 
     * @return name of query item
     */
    String value();
}
