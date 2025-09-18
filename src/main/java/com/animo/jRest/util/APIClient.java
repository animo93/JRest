package com.animo.jRest.util;

import com.animo.jRest.model.APIRequestRecord;
//TODO: Add documentation
public interface APIClient {
    <Response> APIResponse<Response> fetch(final APIRequestRecord apiRequestRecord) throws Exception;
}
