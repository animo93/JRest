package com.animo.jRest;

/**
 * Created by animo on 20/12/17.
 */

public class MyRequestBean<T> {
    private String accessToken;
    private HTTP_METHOD requestType;
    private String url;
    private T requestObject;

    public T getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(T requestObject) {
        this.requestObject = requestObject;
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
}
