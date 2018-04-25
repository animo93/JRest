package com.animo.jRest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author animo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HEADERS {
	String[] value();
}
