package com.animo.jRest.model;

import com.animo.jRest.util.APICallBack;
import com.animo.jRest.util.HTTP_METHOD;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public record APIRequest(
        Optional<String> accessToken,
        HTTP_METHOD httpMethod,
        String url,
        Optional<Object> requestObject,
        Optional<RequestAuthentication> authentication,
        Optional<RequestProxy> proxy,
        Optional<Map<String, String>> headers,
        boolean disableSSLVerification,
        boolean followRedirects,
        Optional<APICallBack<?>> callBack,
        //This will hold the method return type
        Type returnType,
        //This will hold the actual response type inside the APIResponse
        Type responseType
) {}
