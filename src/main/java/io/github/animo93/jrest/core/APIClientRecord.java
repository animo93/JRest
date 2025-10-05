package io.github.animo93.jrest.core;

import java.util.Map;

/**
 * Immutable record to hold APIClient configuration.<br>
 * This is shared across multiple APIRequestRecord instances
 * @param baseUrl
 * @param queryParams
 * @param auth
 * @param reqProxy
 * @param disableSSLVerification
 */
public record APIClientRecord(
        String baseUrl,
        Map<String, String> queryParams,
        RequestAuthenticationRecord auth,
        RequestProxyRecord reqProxy,
        boolean disableSSLVerification
) {}
