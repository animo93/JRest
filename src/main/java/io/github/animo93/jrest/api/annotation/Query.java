package io.github.animo93.jrest.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query parameter appended to the URL.
 *
 * <p>Values are converted to strings and then URL encoded.
 * {@code null} values are ignored. Passing a {@link java.util.List List} or array will result in a
 * query parameter for each non-{@code null} item.
 *
 * <p>Simple Example:
 *
 * <pre>
 * {@code
 * @REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
 * APIResponse<Void,Map<String,Object>> getSingleParamHeadersCall(@Query("page") int page);
 * }
 * </pre>
 *
 * Calling with {@code getSingleParamHeadersCall(1)} yields {@code /get?page=1}.
 * <br>
 * Calling with {@code getSingleParamHeadersCall(null)} yields {@code /get}.
 *
 * <p>Parameter names and values are URL encoded by default. Specify {@link #encoded() encoded=true}
 * to change this behavior.
 *
 * <pre>
 * {@code
 * @REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
 * APIResponse<Void,Map<String,Object>> getSingleParamHeadersCall(@Query(value="group", encoded=true) String group);
 * }
 * </pre>
 *
 * Calling with {@code getSingleParamHeadersCall("foo+bar"))} yields {@code /get?group=foo+bar}.
 * @author animo
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Query {
	String value();
	boolean encoded() default false;
}
