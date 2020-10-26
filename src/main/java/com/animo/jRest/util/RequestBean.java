package com.animo.jRest.util;

import java.util.Map;

public class RequestBean<T> {
	
	private String accessToken;
	private HTTP_METHOD requestType;
	private String url;
	private T requestObject;
	private RequestAuthentication authentication;
	private RequestProxy proxy;
	private Map<String, String> headers;
	private boolean disableSSLVerification;
	private boolean followRedirects = true;
	
	
	
	public boolean isFollowRedirects() {
		return followRedirects;
	}
	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}
	public boolean isDisableSSLVerification() {
		return disableSSLVerification;
	}
	public void setDisableSSLVerification(boolean disableSSLVerification) {
		this.disableSSLVerification = disableSSLVerification;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public HTTP_METHOD getRequestType() {
		return requestType;
	}
	public void setRequestType(HTTP_METHOD requestType) {
		this.requestType = requestType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public T getRequestObject() {
		return requestObject;
	}
	public void setRequestObject(T requestObject) {
		this.requestObject = requestObject;
	}
	public RequestAuthentication getAuthentication() {
		return authentication;
	}
	public void setAuthentication(RequestAuthentication authentication) {
		this.authentication = authentication;
	}
	public RequestProxy getProxy() {
		return proxy;
	}
	public void setProxy(RequestProxy proxy) {
		this.proxy = proxy;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	

}
