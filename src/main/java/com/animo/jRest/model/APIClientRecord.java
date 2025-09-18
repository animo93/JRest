package com.animo.jRest.model;

import java.util.Map;

public record APIClientRecord(
        String baseUrl,
        Map<String, String> queryParams,
        RequestAuthentication auth,
        RequestProxy reqProxy,
        boolean disableSSLVerification
) {}
