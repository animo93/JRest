package com.animo.jRest.model;

import java.util.Map;

public record APIRequestRecord(String baseUrl,
                               Map<String, String> params,
                               RequestAuthentication auth,
                               RequestProxy reqProxy,
                               boolean disableSSLVerification) {
}
