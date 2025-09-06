package com.animo.jRest.model;

import java.util.Map;

import com.animo.jRest.util.HTTP_METHOD;
import lombok.Getter;
import lombok.Setter;
//TODO: Convert to record
@Getter @Setter
public class RequestBean<T> {

	private String accessToken;
    //TODO: Rename to httpMethod
	private HTTP_METHOD requestType;
	private String url;
	private T requestObject;
	private RequestAuthentication authentication;
	private RequestProxy proxy;
	private Map<String, String> headers;
	private boolean disableSSLVerification;
	private boolean followRedirects = true;

}
