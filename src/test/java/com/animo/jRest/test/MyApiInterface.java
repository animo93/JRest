package com.animo.jRest.test;

import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.PATH;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.HTTP_METHOD;

/**
 * Created by animo on 23/12/17.
 */

public interface MyApiInterface {

    @REQUEST(endpoint = "/query",type = HTTP_METHOD.GET)
    @HEADERS("fasdfdasf:fdfasdfa")
    APICall<Void,ApiResponse> testCall(@PATH(value = "function") String function,
			@PATH(value = "symbol") String symbol,
			@PATH(value = "apikey") String apiKey);
   

}
