package com.animo.jRest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation on a service method when you want to follow/unfollow the redirect URL of an API
 * 
 * <p>Note : Absence of this results in the default setting of following redirects
 * @author animo
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FollowRedirects {

	boolean value() default true;

}
