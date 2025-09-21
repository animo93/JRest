package com.jrest.java;

import java.util.Map;

import com.jrest.java.annotation.*;
import com.jrest.java.util.APICallBack;
import com.jrest.java.util.APIResponse;
import com.jrest.java.util.HTTP_METHOD;

public interface TestPostmanEchoAPIInterface {
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS("X-Foo:Bar")
    APIResponse<Map<String, Object>> getCall();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS({
		"X-Foo:Bar",
		"X-Ping:Pong"
	})
    APIResponse<Map<String, Object>> getMultipleHeadersCall();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS("X-FooBar")
    APIResponse<Map<String, Object>> getIncorrectHeader();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
    APIResponse<Map<String, Object>> getSingleParamHeadersCall(@HeaderMap Map<String, String> header);
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS({
		"X-Ping:Pong",
		"X-Static:True"
	})
    APIResponse<Map<String, Object>> getBothSingleParamStaticHeadersCall(@HeaderMap Map<String, String> header);
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
    APIResponse<Map<String, Object>> noHeadersCall();
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String, Object>> incorrectHeadersCall(@HeaderMap String headers);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> singleQueryParamsCall(@Query("foo") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> multipleQueryParamsCall(@Query("foo") String bar,
                                                                   @Query("ping") String pong);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> singleQueryParamsFailureCall(@Query("foo") int bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> emptyQueryKeyFailureCall(@Query("") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> queryKeyWithSpaceCall(@Query("query key") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> unEncodedqueryKeyWithEncodedSetTrueCall(@Query(value="query key",encoded=true) String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> encodedqueryKeyWithEncodedSetTrueCall(@Query(value="query+key",encoded=true) String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> singleQueryMapCall(@QueryMap Map<String,String> queryMap);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> multipleQueryMapCall(@QueryMap Map<String,String> queryMap1,
                                                                @QueryMap Map<String,String> queryMap2);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> bothQueryAndQueryMapCall(@QueryMap Map<String,String> queryMap ,
                                                                    @Query("ping") String pong);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
    APIResponse<Map<String,Object>> singleQueryMapFailureCall(@QueryMap() int bar);

	@REQUEST(endpoint = "/post",type = HTTP_METHOD.POST)
	@HEADERS("Content-Type: application/json")
    APIResponse<Map<String,Object>> requestBodyObjectCall(@Body TestRequestBody requestBody);

	@REQUEST(endpoint = "/post",type = HTTP_METHOD.POST)
	@HEADERS("Content-Type: application/json")
    APIResponse<Map<String,Object>> requestBodyMapCall(@Body Map<String,Object> requestBody);

    @REQUEST(endpoint = "/get",type = HTTP_METHOD.GET)
    @HEADERS("Content-Type: application/json")
    APIResponse<String> responseAsString();

    @REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
    @HEADERS("X-Foo:Bar")
    void asyncCall(APICallBack<Map<String, Object>> callBack);

}
