package com.animo.jRest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.animo.jRest.util.HTTP_METHOD;

/**
 * Created by animo on 25/12/17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface REQUEST {
    String endpoint();
    HTTP_METHOD type();
}
