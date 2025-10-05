package io.github.animo93.jrest.e2e;

import io.github.animo93.jrest.api.annotation.Query;
import io.github.animo93.jrest.api.annotation.QueryMap;
import io.github.animo93.jrest.api.annotation.REQUEST;
import io.github.animo93.jrest.api.APIResponse;
import io.github.animo93.jrest.api.HTTP_METHOD;
import io.github.animo93.jrest.api.JRestDynamicAPiInterface;

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
