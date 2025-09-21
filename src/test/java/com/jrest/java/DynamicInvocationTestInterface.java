package com.jrest.java;

import com.jrest.java.annotation.Query;
import com.jrest.java.annotation.QueryMap;
import com.jrest.java.annotation.REQUEST;
import com.jrest.java.util.APIResponse;
import com.jrest.java.util.HTTP_METHOD;
import com.jrest.java.util.JRestDynamicAPiInterface;

import java.util.Map;

public interface DynamicInvocationTestInterface extends JRestDynamicAPiInterface {

    @REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
    APIResponse<Map<String, Object>> noHeadersCall();

    @REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> bothQueryAndQueryMapCall(@QueryMap Map<String,String> queryMap ,
                                                                    @Query("ping") String pong);

    @REQUEST(endpoint = "/get",type= HTTP_METHOD.GET)
    APIResponse<TestAPIResponse> bothQueryAndQueryMapCallWithResponse(@QueryMap Map<String,String> queryMap ,
                                                                      @Query("ping") String pong);

}
