package com.animo.jRest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds headers dynamically via parameters , literally supplied via a Map&#60;String,String&#62;
 *
 * <pre><code>
 * 
 * &#64;REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall&#60;Void,Map&#60;String,Object&#62;&#62; getSingleParamHeadersCall(@HEADER Map&#60;String, String&#62; header);
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
