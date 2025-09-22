package com.jrest.java.e2e;

import com.jrest.java.api.annotation.Query;
import com.jrest.java.api.annotation.QueryMap;
import com.jrest.java.api.annotation.REQUEST;
import com.jrest.java.api.APIResponse;
import com.jrest.java.api.HTTP_METHOD;
import com.jrest.java.api.JRestDynamicAPiInterface;

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
