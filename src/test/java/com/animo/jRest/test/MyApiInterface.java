package com.animo.jRest.test;

import java.util.Map;

import com.animo.jRest.HEADERS;
import com.animo.jRest.HTTP_METHOD;
import com.animo.jRest.MyCall;
import com.animo.jRest.REQUEST;

/**
 * Created by animo on 23/12/17.
 */

public interface MyApiInterface {

    @REQUEST(url = "",type = HTTP_METHOD.GET)
    @HEADERS("fasdfdasf:fdfasdfa")
    MyCall<Void,Map<String, String>> testCall(String accessToken,String url);

}
