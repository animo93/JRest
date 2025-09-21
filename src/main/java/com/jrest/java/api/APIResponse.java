package com.jrest.java.api;

import java.util.List;
import java.util.Map;

/**
 * Generic record to hold the API response details
 * @param response
 * @param responseCode
 * @param responseHeaders
 * @param <Response>
 */
public record APIResponse<Response>(
        Response response,
        int responseCode,
        Map<String, List<String>> responseHeaders
) {}
