package com.animo.jRest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds headers dynamically via parameters , literally supplied via a Map<String,String>
 *
 * <pre><code>
 * 
 * &#64;REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> getSingleParamHeadersCall(@HEADER Map<String, String> header);
 * </code></pre>
 *
 * @author animo
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface HEADER {
}
