package com.jrest.java.util;

import com.jrest.java.model.APIRequestRecord;
//TODO: Add documentation
public interface APIClient {
    <Response> APIResponse<Response> fetch(final APIRequestRecord apiRequestRecord) throws Exception;
}
