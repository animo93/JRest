package com.animo.jRest.annotation;

import java.lang.annotation.*;

/**
 * Named replacement in a URL query parameters segment. Values are converted to strings using the Parameters passed to the request Method
 * <p>For Example :
 * <pre><code>
 * &#64;REQUEST(endpoint = "/book",type = HTTP_METHOD.GET)
 * APICall<Void,ApiResponse> getSingleQParamCall(@Query(value = "author") String author);
 * </code></pre>
 * @author harshit
 *
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String value();
}
