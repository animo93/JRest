package com.animo.jRest.test;

import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.HTTP_METHOD;

import java.util.Map;

public interface HttpApiInterface {

    @REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
    APIRequest<Map<String, Object>> getCall();
}
