package com.animo.jRest.test;

import java.util.Map;

import com.animo.jRest.annotation.HEADER;
import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.HTTP_METHOD;

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
	
	@REQUEST(endpoint = "/get", type = HTTP_METHOD.GET)
	APICall<Void, Map<String, Object>> incorrectHeadersCall(@HEADER String headers);

}
