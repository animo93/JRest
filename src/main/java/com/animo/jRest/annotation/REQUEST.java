package com.animo.jRest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.animo.jRest.util.HTTP_METHOD;

/**
 * Use this mandatory annotation to specify the HTTP Method type 
 * and the endpoint URL for making the connection 
 * <p>For Example :
 * <pre><code>
 * &#64;REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
 * APICall&#60;Void,ApiResponse&#62; listRepos(@PATH(value = "user") String user);
 * </code></pre>
 * @author animo
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface REQUEST {
    String endpoint();
    HTTP_METHOD type();
}
