package io.github.animo93.jrest.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Named replacement in a URL path segment. Values are converted to strings using the Parameters passed to the request Method
 * <p>For Example :
 * <pre>
 * {@code @REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
 * APIResponse<Void,Map<String,Object>> listRepos(@Path(value = "user") String user);}
 * </pre>
 *
 * @author animo
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    String value();
}
