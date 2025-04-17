package com.animo.jRest.test;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APIHelper;
import com.animo.jRest.util.APIResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unchecked")
public class QueryParamTest {
	

	@Test
	public void testSingleQueryParam() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.singleQueryParamsCall("bar");
		APIResponse<Map<String,Object>> response = call.callMeNow();
		
		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
		
	}
	

	@Test
	public void testMultipleQueryParam() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.multipleQueryParamsCall("bar","pong");
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.getResponse().get("args")).get("ping"));
		
	}

	@Test
	public void testMultipleQueryParamWithGlobalParams() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.addParameter("ping", "pong")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.singleQueryParamsCall("bar");
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.getResponse().get("args")).get("ping"));
		
	}

	@Test
	public void testNullQueryParam() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.singleQueryParamsCall(null);
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("https://postman-echo.com/get", call.requestBean().getUrl());
		Assertions.assertTrue(((Map<String,String>) response.getResponse().get("args")).isEmpty());
		
	}

	@Test
	public void testInvalidQueryParamFailure() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		
		
		Exception exception = Assertions.assertThrows(InvalidParameterException.class, () -> {
			APICall<Map<String,Object>> call = testInterface.singleQueryParamsFailureCall(0);
			APIResponse<Map<String,Object>> response = call.callMeNow();
		});

		Assertions.assertTrue(exception.getMessage().contains("Query parameter should be passed in string format only"));
		
	}

	@Test
	public void testEmptyQueryParamKeyFailure() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.emptyQueryKeyFailureCall("bar");
		APIResponse<Map<String,Object>> response = call.callMeNow();


		Assertions.assertEquals("https://postman-echo.com/get", call.requestBean().getUrl());
		Assertions.assertTrue(((Map<String,String>) response.getResponse().get("args")).isEmpty());
		
	}
	

	@Test
	public void testQueryParamKeyWithSpace() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.queryKeyWithSpaceCall("bar");
		APIResponse<Map<String,Object>> response = call.callMeNow();
		
		
		//Assert.assertEquals("https://postman-echo.com/get", call.getRequestBean().getUrl());		
		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("query key"));
		
	}

	@Test
	public void testUnEncodedqueryKeyWithEncodedSetTrue_shouldThrowException() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.unEncodedqueryKeyWithEncodedSetTrueCall("bar");
		assertThrows(ExecutionException.class, () -> call.callMeNow());
		
	}
	
	@Test
	public void testEncodedqueryKeyWithEncodedSetTrue() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.encodedqueryKeyWithEncodedSetTrueCall("bar");
		APIResponse<Map<String,Object>> response = call.callMeNow();


		Assertions.assertEquals("https://postman-echo.com/get?query+key=bar", call.requestBean().getUrl());
		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("query key"));
		
	}

	@Test
	public void testSingleQueryMap() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");
		queryMap.put("ping","pong");
		APICall<Map<String,Object>> call = testInterface.singleQueryMapCall(queryMap);
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.getResponse().get("args")).get("ping"));
		
	}

	@Test
	public void testMultipleQueryMap() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");
		queryMap.put("ping","pong");
		
		Map<String,String> queryMap2 = new HashMap<String,String>();
		queryMap2.put("tik", "tok");
		queryMap2.put("john","doe");
		
		APICall<Map<String,Object>> call = testInterface.multipleQueryMapCall(queryMap,queryMap2);
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
		Assertions.assertEquals("pong",((Map<String,String>) response.getResponse().get("args")).get("ping"));
		Assertions.assertEquals("tok", ((Map<String,String>) response.getResponse().get("args")).get("tik"));
		Assertions.assertEquals("doe", ((Map<String,String>) response.getResponse().get("args")).get("john"));
		
	}

	@Test
	public void testBothSingleQueryAndQueryMap() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");
		
		APICall<Map<String,Object>> call = testInterface.bothQueryAndQueryMapCall(queryMap, "pong");
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.getResponse().get("args")).get("ping"));
		
	}

	@Test
	public void testQueryMapAndQueryWithGlobalParams() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.addParameter("tik", "tok")
				.build();
		
		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");
		
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		
		APICall<Map<String,Object>> call = testInterface.bothQueryAndQueryMapCall(queryMap, "pong");
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.getResponse().get("args")).get("ping"));
		Assertions.assertEquals("tok", ((Map<String,String>) response.getResponse().get("args")).get("tik"));
		
	}

	@Test
	public void testInvalidQueryMapFailure() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		
		
		Exception exception = Assertions.assertThrows(InvalidParameterException.class, () -> {
			APICall<Map<String,Object>> call = testInterface.singleQueryMapFailureCall(0);
			APIResponse<Map<String,Object>> response = call.callMeNow();
		});

		Assertions.assertTrue(exception.getMessage().contains("Query parameter should be passed in Map format only"));
		
	}

	@Test
	public void testNullQueryMap() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testInterface.singleQueryMapCall(null);
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("https://postman-echo.com/get", call.requestBean().getUrl());
		Assertions.assertTrue(((Map<String,String>) response.getResponse().get("args")).isEmpty());
		
	}

	@Test
	public void testAddAllParams() throws Exception {
		Map<String,String> queryParamMap = new HashMap<>();
		queryParamMap.put("foo","bar");

		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.addAllParameters(queryParamMap)
				.build();
		TestPostmanEchoAPIInterface testPostmanEchoAPIInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Map<String,Object>> call = testPostmanEchoAPIInterface.getCall();
		APIResponse<Map<String,Object>> response = call.callMeNow();

		Assertions.assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));

	}
	

}
