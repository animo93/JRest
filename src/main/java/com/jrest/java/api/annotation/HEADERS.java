package com.jrest.java.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds headers literally supplied in the {@code value}.
 *
 * <pre><code>
 * &#64;Headers("Cache-Control: max-age=640000")
 *
 * &#64;Headers({
 *   "X-Foo: Bar",
 *   "X-Ping: Pong"
 * })
 * </code></pre>
 *
 * @author animo
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HEADERS {
	String[] value();
}
