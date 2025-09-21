package com.jrest.java.core;

import com.jrest.java.api.APIResponse;

//TODO: Add documentation
public interface APIClient {
    <Response> APIResponse<Response> fetch(final APIRequestRecord apiRequestRecord) throws Exception;
}
