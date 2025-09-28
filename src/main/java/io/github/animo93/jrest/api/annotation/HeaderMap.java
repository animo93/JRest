package io.github.animo93.jrest.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds headers dynamically via parameters , literally supplied via a {@code Map<String,String>}
 *
 * <pre>
 * {@code @REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
 * APIResponse<Void,Map<String,Object>> getSingleParamHeadersCall(@HeaderMap Map<String, String>; header);}
 * </pre>
 *
 * @author animo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface HeaderMap {
}
