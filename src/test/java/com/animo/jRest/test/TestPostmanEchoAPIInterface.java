package com.animo.jRest.test;

import java.util.Map;

import com.animo.jRest.annotation.*;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.HTTP_METHOD;
import com.animo.jRest.util.JRestDynamicAPiInterface;

public interface TestPostmanEchoAPIInterface {
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS("X-Foo:Bar")
	APICall<Void, Map<String, Object>> getCall();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS({
		"X-Foo:Bar",
		"X-Ping:Pong"
	})
	APICall<Void, Map<String, Object>> getMultipleHeadersCall();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS("X-FooBar")
	APICall<Void, Map<String, Object>> getIncorrectHeader();
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	APICall<Void, Map<String, Object>> getSingleParamHeadersCall(@HEADER Map<String, String> header);
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	@HEADERS({
		"X-Ping:Pong",
		"X-Static:True"
	})
	APICall<Void, Map<String, Object>> getBothSingleParamStaticHeadersCall(@HEADER Map<String, String> header);
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	APICall<Void, Map<String, Object>> noHeadersCall();
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String, Object>> incorrectHeadersCall(@HEADER String headers);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> singleQueryParamsCall(@Query("foo") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> multipleQueryParamsCall(@Query("foo") String bar, 
			@Query("ping") String pong);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> singleQueryParamsFailureCall(@Query("foo") int bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> emptyQueryKeyFailureCall(@Query("") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> queryKeyWithSpaceCall(@Query("query key") String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> unEncodedqueryKeyWithEncodedSetTrueCall(@Query(value="query key",encoded=true) String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> encodedqueryKeyWithEncodedSetTrueCall(@Query(value="query+key",encoded=true) String bar);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> singleQueryMapCall(@QueryMap Map<String,String> queryMap);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> multipleQueryMapCall(@QueryMap Map<String,String> queryMap1,
			@QueryMap Map<String,String> queryMap2);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> bothQueryAndQueryMapCall(@QueryMap Map<String,String> queryMap , 
			@Query("ping") String pong);
	
	@REQUEST(endpoint = "/get",type=HTTP_METHOD.GET)
	APICall<Void,Map<String,Object>> singleQueryMapFailureCall(@QueryMap() int bar);

	@REQUEST(endpoint = "/post",type = HTTP_METHOD.POST)
	@HEADERS("Content-Type: application/json")
	APICall<TestRequestBody,Map<String,Object>> requestBodyObjectCall(@Body TestRequestBody requestBody);

	@REQUEST(endpoint = "/post",type = HTTP_METHOD.POST)
	@HEADERS("Content-Type: application/json")
	APICall<Void,Map<String,Object>> requestBodyMapCall(@Body Map<String,Object> requestBody);

}
