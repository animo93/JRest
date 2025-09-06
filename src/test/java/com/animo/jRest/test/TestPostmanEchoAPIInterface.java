package com.animo.jRest.test;

import java.util.Map;

import com.animo.jRest.annotation.*;
import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.HTTP_METHOD;

public interface TestPostmanEchoAPIInterface {
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS("X-Foo:Bar")
	APIRequest<Map<String, Object>> getCall();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS({
		"X-Foo:Bar",
		"X-Ping:Pong"
	})
	APIRequest<Map<String, Object>> getMultipleHeadersCall();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS("X-FooBar")
	APIRequest<Map<String, Object>> getIncorrectHeader();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	APIRequest<Map<String, Object>> getSingleParamHeadersCall(@HeaderMap Map<String, String> header);
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS({
		"X-Ping:Pong",
		"X-Static:True"
	})
	APIRequest<Map<String, Object>> getBothSingleParamStaticHeadersCall(@HeaderMap Map<String, String> header);
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	APIRequest<Map<String, Object>> noHeadersCall();
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String, Object>> incorrectHeadersCall(@HeaderMap String headers);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> singleQueryParamsCall(@Query("foo") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> multipleQueryParamsCall(@Query("foo") String bar,
														   @Query("ping") String pong);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> singleQueryParamsFailureCall(@Query("foo") int bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> emptyQueryKeyFailureCall(@Query("") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> queryKeyWithSpaceCall(@Query("query key") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> unEncodedqueryKeyWithEncodedSetTrueCall(@Query(value="query key",encoded=true) String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> encodedqueryKeyWithEncodedSetTrueCall(@Query(value="query+key",encoded=true) String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> singleQueryMapCall(@QueryMap Map<String,String> queryMap);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> multipleQueryMapCall(@QueryMap Map<String,String> queryMap1,
														@QueryMap Map<String,String> queryMap2);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> bothQueryAndQueryMapCall(@QueryMap Map<String,String> queryMap ,
															@Query("ping") String pong);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APIRequest<Map<String,Object>> singleQueryMapFailureCall(@QueryMap() int bar);

	@REQUEST(endpoint = "/post",type = HTTP_METHOD.POST)
	@HEADERS("Content-Type: application/json")
	APIRequest<Map<String,Object>> requestBodyObjectCall(@Body TestRequestBody requestBody);

	@REQUEST(endpoint = "/post",type = HTTP_METHOD.POST)
	@HEADERS("Content-Type: application/json")
	APIRequest<Map<String,Object>> requestBodyMapCall(@Body Map<String,Object> requestBody);

    @REQUEST(endpoint = "/get",type = HTTP_METHOD.GET)
    @HEADERS("Content-Type: application/json")
    APIRequest<String> responseAsString();

}
