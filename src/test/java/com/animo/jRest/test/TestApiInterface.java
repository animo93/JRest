package com.animo.jRest.test;

import com.animo.jRest.annotation.FollowRedirects;
import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.HTTP_METHOD;

/**
 * Created by animo on 23/12/17.
 */

public interface TestApiInterface {

    @REQUEST(endpoint = "", type = HTTP_METHOD.GET)
    @HEADERS("fasdfdasf:fdfasdfa")
    @FollowRedirects(false)
    APICall<Void, String> testFollowRedirectFalse();
    
    @REQUEST(endpoint = "", type = HTTP_METHOD.GET)
    @HEADERS("fasdfdasf:fdfasdfa")
    @FollowRedirects()
    APICall<Void, String> testFollowRedirectTrue();
    
    @REQUEST(endpoint = "", type = HTTP_METHOD.GET)
    @HEADERS("fasdfdasf:fdfasdfa")
    APICall<Void, String> testFollowRedirectNone();
   

}
