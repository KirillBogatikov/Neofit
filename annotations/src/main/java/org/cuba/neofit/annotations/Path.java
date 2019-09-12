package org.cuba.neofit.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks method's parameter as Path variable<br>
 * URL of service's method can contains variables<br>
 * which will be replaces by real values at building<br>
 * request building
 * <p>Example:<br>
 * <pre><code>
 *     @Get("localhost/service/{name}/{property}")
 *     public NeoCall get(@Path("name") String param1, @Path("property") String param2);
 * </code></pre>
 * <p>Variables <code>{name}</code> and <code>{property}</code> will be<br>
 * replaces with values of method's parameters <code>param1</code> and<br>
 * <code>param2</code> respectively.
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Path {
    /**
     * Name of 
     * @return
     */
    String value();
}   
