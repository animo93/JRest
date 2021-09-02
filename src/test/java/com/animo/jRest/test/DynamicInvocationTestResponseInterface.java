package com.animo.jRest.test;

import com.animo.jRest.annotation.Query;
import com.animo.jRest.annotation.QueryMap;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.HTTP_METHOD;
import com.animo.jRest.util.JRestDynamicAPiInterface;

import java.util.Map;

public interface DynamicInvocationTestResponseInterface extends JRestDynamicAPiInterface<TestAPIResponse> {

    @REQUEST(endpoint = "/get",type= HTTP_METHOD.GET)
    APICall<Void,TestAPIResponse> bothQueryAndQueryMapCallWithResponse(@QueryMap Map<String,String> queryMap ,
                                                                       @Query("ping") String pong);
}
