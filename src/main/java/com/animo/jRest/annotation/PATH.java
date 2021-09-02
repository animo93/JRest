package com.animo.jRest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Named replacement in a URL path segment. Values are converted to strings using the Parameters passed to the request Method
 * <p>For Example :
 * <pre><code>
 * &#64;REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
 * APICall&#60;Void,ApiResponse&#62; listRepos(@PATH(value = "user") String user);
 * </code></pre>
 * @author animo
 *
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PATH {
    String value();
}
