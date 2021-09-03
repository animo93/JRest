package com.animo.jRest.test;

import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.HTTP_METHOD;

import java.util.Map;

public interface HttpApiInterface {

    @REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
    APICall<Void, Map<String, Object>> getCall();
}
