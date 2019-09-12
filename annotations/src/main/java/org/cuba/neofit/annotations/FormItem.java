package org.cuba.neofit.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks method's parameter as one item of request's form body<br>
 * All items in form data body stored in <i>key/value</i> pairs
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface FormItem {
    /**
     * Describes name of this form data item
     * @return name of form data item
     */
    String value();
}
