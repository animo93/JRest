package com.animo.jRest.model;

import com.animo.jRest.util.APICallBack;
import com.animo.jRest.util.HTTP_METHOD;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
/**
 * Immutable record to hold APIRequest configuration.<br>
 * This is created per API method invocation
 * @param accessToken
 * @param httpMethod
 * @param url
 * @param requestObject
 * @param authentication
 * @param proxy
 * @param headers
 * @param disableSSLVerification
 * @param followRedirects
 * @param callBack
 * @param returnType
 * @param responseType
 */
public record APIRequestRecord(
        Optional<String> accessToken,
        HTTP_METHOD httpMethod,
        String url,
        Optional<Object> requestObject,
        Optional<RequestAuthenticationRecord> authentication,
        Optional<RequestProxyRecord> proxy,
        Optional<Map<String, String>> headers,
        boolean disableSSLVerification,
        boolean followRedirects,
        Optional<APICallBack<?>> callBack,
        //This will hold the method return type
        Type returnType,
        //This will hold the actual response type inside the APIResponse
        Type responseType
) {}
