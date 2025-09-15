package com.animo.jRest.model;

import java.util.Map;
//TODO: These values shall remain constant throughout the lifecycle of the application and would need an apt name
public record APIRequestRecord(String baseUrl,
                               Map<String, String> queryParams,
                               RequestAuthentication auth,
                               RequestProxy reqProxy,
                               boolean disableSSLVerification) {
}
