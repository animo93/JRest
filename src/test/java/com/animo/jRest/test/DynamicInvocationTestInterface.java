package com.animo.jRest.test;

import com.animo.jRest.annotation.Query;
import com.animo.jRest.annotation.QueryMap;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.HTTP_METHOD;
import com.animo.jRest.util.JRestDynamicAPiInterface;

import java.util.Map;

public interface DynamicInvocationTestInterface extends JRestDynamicAPiInterface<Map<String,Object>> {

    @REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
    APICall<Void, Map<String, Object>> noHeadersCall();

    @REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APICall<Void,Map<String,Object>> bothQueryAndQueryMapCall(@QueryMap Map<String,String> queryMap ,
                                                              @Query("ping") String pong);

}
