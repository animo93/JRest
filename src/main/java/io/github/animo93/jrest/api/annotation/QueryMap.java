package io.github.animo93.jrest.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Query parameter keys and values appended to the URL.
 *
 * <p>Values are converted to strings using
 * {@link Object#toString()}, if no matching string converter is installed).
 *
 * <p>Simple Example:
 * <pre>
 * {@code
 * @REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
 * APIResponse<Void,Map<String,Object>> getSingleParamHeadersCall(@QueryMap Map<String,String> page);
 * }
 * </pre>
 * Calling with {@code getSingleParamHeadersCall(ImmutableMap.of("group", "coworker", "age", "42"))} yields {@code
 * /get?group=coworker&age=42}.
 *
 * <p>Map keys and values representing parameter values are URL encoded by default. Specify {encoded=true} to change this behavior.
 * <p>A {@code null} value for the map, as a key, or as a value is not allowed.
 * 
 * @author animo
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface QueryMap {

}
