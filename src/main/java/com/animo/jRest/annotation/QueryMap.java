package com.animo.jRest.annotation;

import java.lang.annotation.*;

/**
 * Adds query parameters dynamically via parameters , literally supplied via a Map<String,String>
 *
 * <pre><code>
 *
 * &#64;REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
 APICall<Void,Map<String,Object>> getQParamMapCall(@QueryMap Map<String, String> queryMap);
 * </code></pre>
 *
 * @author harshit
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface QueryMap {
}
