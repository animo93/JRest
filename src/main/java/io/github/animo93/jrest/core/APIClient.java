package io.github.animo93.jrest.core;

import io.github.animo93.jrest.api.APIResponse;

/**
 * The APIClient interface defines a method for fetching API responses based on provided request records.
 * It serves as a contract for implementing classes to handle API requests and return corresponding responses.
 *
 * @author animo
 */
public interface APIClient {
    <Response> APIResponse<Response> fetch(final APIRequestRecord apiRequestRecord) throws Exception;
}
