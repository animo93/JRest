package com.animo.jRest.test;

import java.util.Map;

import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.HTTP_METHOD;
import com.animo.jRest.util.APICall;

/**
 * Created by animo on 23/12/17.
 */

public interface MyApiInterface {

    @REQUEST(endpoint = "",type = HTTP_METHOD.GET)
    @HEADERS("fasdfdasf:fdfasdfa")
    APICall<Void,Map<String, String>> testCall();

}
