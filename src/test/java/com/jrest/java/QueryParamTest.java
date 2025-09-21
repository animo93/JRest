package com.jrest.java;

import com.jrest.java.api.JRest;
import com.jrest.java.api.APIResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unchecked")
public class QueryParamTest {
	

	@Test
	public void testSingleQueryParam() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.singleQueryParamsCall("bar");
		
		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));
		
	}
	

	@Test
	public void testMultipleQueryParam() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.multipleQueryParamsCall("bar","pong");

		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.response().get("args")).get("ping"));
		
	}

	@Test
	public void testMultipleQueryParamWithGlobalParams() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.addQueryParameter("ping", "pong")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.singleQueryParamsCall("bar");

		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.response().get("args")).get("ping"));
		
	}

	@Test
	public void testNullQueryParam() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.singleQueryParamsCall(null);

		//Assertions.assertEquals("https://postman-echo.com/get", call.requestBean().getUrl());
		Assertions.assertTrue(((Map<String,String>) response.response().get("args")).isEmpty());
		
	}

	@Test
	public void testInvalidQueryParamFailure() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		
		Exception exception = Assertions.assertThrows(InvalidParameterException.class, () -> {
			APIResponse<Map<String,Object>> response = testInterface.singleQueryParamsFailureCall(0);
		});

		Assertions.assertTrue(exception.getMessage().contains("Query parameter should be passed in string format only"));
		
	}

	@Test
	public void testEmptyQueryParamKeyFailure() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.emptyQueryKeyFailureCall("bar");


		//Assertions.assertEquals("https://postman-echo.com/get", call.requestBean().getUrl());
		Assertions.assertTrue(((Map<String,String>) response.response().get("args")).isEmpty());
		
	}
	

	@Test
	public void testQueryParamKeyWithSpace() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.queryKeyWithSpaceCall("bar");
		
		
		//Assert.assertEquals("https://postman-echo.com/get", call.getRequestBean().getUrl());		
		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("query key"));
		
	}

	@Test
	public void testUnEncodedqueryKeyWithEncodedSetTrue_shouldThrowException() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

        assertThrows(URISyntaxException.class, () -> {
            try {
                testInterface.unEncodedqueryKeyWithEncodedSetTrueCall("bar");
            } catch (java.lang.reflect.UndeclaredThrowableException e) {
                Throwable cause = e.getCause().getCause();
                if (cause instanceof URISyntaxException) {
                    throw (URISyntaxException) cause;
                }
                throw e;
            }
        });
		
	}
	
	@Test
	public void testEncodedqueryKeyWithEncodedSetTrue() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.encodedqueryKeyWithEncodedSetTrueCall("bar");


		//Assertions.assertEquals("https://postman-echo.com/get?query+key=bar", call.requestBean().getUrl());
		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("query key"));
		
	}

	@Test
	public void testSingleQueryMap() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");
		queryMap.put("ping","pong");

		APIResponse<Map<String,Object>> response = testInterface.singleQueryMapCall(queryMap);

		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.response().get("args")).get("ping"));
		
	}

	@Test
	public void testMultipleQueryMap() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");
		queryMap.put("ping","pong");
		
		Map<String,String> queryMap2 = new HashMap<String,String>();
		queryMap2.put("tik", "tok");
		queryMap2.put("john","doe");

		APIResponse<Map<String,Object>> response = testInterface.multipleQueryMapCall(queryMap,queryMap2);

		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));
		Assertions.assertEquals("pong",((Map<String,String>) response.response().get("args")).get("ping"));
		Assertions.assertEquals("tok", ((Map<String,String>) response.response().get("args")).get("tik"));
		Assertions.assertEquals("doe", ((Map<String,String>) response.response().get("args")).get("john"));
		
	}

	@Test
	public void testBothSingleQueryAndQueryMap() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");

		APIResponse<Map<String,Object>> response = testInterface.bothQueryAndQueryMapCall(queryMap, "pong");

		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.response().get("args")).get("ping"));
		
	}

	@Test
	public void testQueryMapAndQueryWithGlobalParams() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.addQueryParameter("tik", "tok")
				.build(TestPostmanEchoAPIInterface.class);
		
		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("foo", "bar");

		APIResponse<Map<String,Object>> response = testInterface.bothQueryAndQueryMapCall(queryMap, "pong");

		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));
		Assertions.assertEquals("pong", ((Map<String,String>) response.response().get("args")).get("ping"));
		Assertions.assertEquals("tok", ((Map<String,String>) response.response().get("args")).get("tik"));
		
	}

	@Test
	public void testInvalidQueryMapFailure() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		
		
		Exception exception = Assertions.assertThrows(InvalidParameterException.class, () -> {
			APIResponse<Map<String,Object>> response = testInterface.singleQueryMapFailureCall(0);
		});

		Assertions.assertTrue(exception.getMessage().contains("Query parameter should be passed in Map format only"));
		
	}

	@Test
	public void testNullQueryMap() throws Exception {

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.singleQueryMapCall(null);

		//Assertions.assertEquals("https://postman-echo.com/get", call.requestBean().getUrl());
		Assertions.assertTrue(((Map<String,String>) response.response().get("args")).isEmpty());
		
	}

	@Test
	public void testAddAllParams() throws Exception {
		Map<String,String> queryParamMap = new HashMap<>();
		queryParamMap.put("foo","bar");

        TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.addQueryMap(queryParamMap)
				.build(TestPostmanEchoAPIInterface.class);

		APIResponse<Map<String,Object>> response = testInterface.getCall();

		Assertions.assertEquals("bar", ((Map<String,String>) response.response().get("args")).get("foo"));

	}
	

}
