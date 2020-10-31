package com.animo.jRest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * * Query parameter appended to the URL.
 *
 * <p>Values are converted to strings and then URL encoded.
 * {@code null} values are ignored. Passing a {@link java.util.List List} or array will result in a
 * query parameter for each non-{@code null} item.
 *
 * <p>Simple Example:
 *
 * <pre><code>
 * &#64;GET("/friends")
 * Call&lt;ResponseBody&gt; friends(@Query("page") int page);
 * </code></pre>
 *
 * Calling with {@code foo.friends(1)} yields {@code /friends?page=1}.
 *
 * <p>Example with {@code null}:
 *
 * <pre><code>
 * &#64;GET("/friends")
 * Call&lt;ResponseBody&gt; friends(@Query("group") String group);
 * </code></pre>
 *
 * Calling with {@code foo.friends(null)} yields {@code /friends}.
 *
 * <p>Array/Varargs Example:
 *
 * <pre><code>
 * &#64;GET("/friends")
 * Call&lt;ResponseBody&gt; friends(@Query("group") String... groups);
 * </code></pre>
 *
 * Calling with {@code foo.friends("coworker", "bowling")} yields {@code
 * /friends?group=coworker&group=bowling}.
 *
 * <p>Parameter names and values are URL encoded by default. Specify {@link #encoded() encoded=true}
 * to change this behavior.
 *
 * <pre><code>
 * &#64;GET("/friends")
 * Call&lt;ResponseBody&gt; friends(@Query(value="group", encoded=true) String group);
 * </code></pre>
 *
 * Calling with {@code foo.friends("foo+bar"))} yields {@code /friends?group=foo+bar}.
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
