package com.jrest.java.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jrest.java.api.HTTP_METHOD;

/**
 * Use this mandatory annotation to specify the HTTP Method type 
 * and the endpoint URL for making the connection 
 * <p>For Example :
 * <pre><code>
 * {@code
 * @REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
 * APIResponse<Void,Map<String,Object>> getSingleParamHeadersCall(@QueryMap Map<String,String> page);
 * }
 * </code></pre>
 * @author animo
 *
 */
//TODO: Remove this and instead introduce @GET , @POST , @PUT , @PATCH & @DELETE
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface REQUEST {
    String endpoint();
    HTTP_METHOD type();
}
