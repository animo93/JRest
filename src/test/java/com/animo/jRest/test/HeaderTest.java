package com.animo.jRest.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APIHelper;

public class HeaderTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Ignore
	@Test
	public void testSingleStaticHeaderKey() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Void, Map<String,Object>> testCall = testInterface.getCall();
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		Assert.assertTrue(((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-foo"));
		
	}
	
	@Ignore
	@Test
	public void testSingleStaticHeaderValue() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Void, Map<String,Object>> testCall = testInterface.getCall();
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		Assert.assertEquals("Bar", ((Map<String,String>) response.getResponseBody().get("headers")).get("x-foo"));
	}
	
	@Ignore
	@Test
	public void testMultipleStaticHeaderKey() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Void, Map<String,Object>> testCall = testInterface.getMultipleHeadersCall();
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		Assert.assertTrue(((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-foo") &&
				((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-ping"));
		
	}
	
	@Ignore
	@Test
	public void testMultipleStaticHeaderValue() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Void, Map<String,Object>> testCall = testInterface.getMultipleHeadersCall();
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		Assert.assertEquals("Bar", ((Map<String,String>) response.getResponseBody().get("headers")).get("x-foo"));
		Assert.assertEquals("Pong", ((Map<String,String>) response.getResponseBody().get("headers")).get("x-ping"));
	}
	
	@Ignore
	@Test
	public void testFailureStaticHeader() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();		
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Header data invalid");
		
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Void, Map<String,Object>> testCall = testInterface.getIncorrectHeader();
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		
	}
	@Ignore
	@Test
	public void testSingleDynammicHeaderKey() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> testMap = new HashMap<String, String>();
		testMap.put("x-Foo", "Bar");
		APICall<Void, Map<String,Object>> testCall = testInterface.getSingleParamHeadersCall(testMap);
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		Assert.assertTrue(((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-foo"));
		
	}
	@Ignore
	@Test
	public void testSingleDynammicHeaderValue() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> testMap = new HashMap<String, String>();
		testMap.put("x-Foo", "Bar");
		APICall<Void, Map<String,Object>> testCall = testInterface.getSingleParamHeadersCall(testMap);
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		
		Assert.assertEquals("Bar", ((Map<String,String>) response.getResponseBody().get("headers")).get("x-foo"));
		
	}
	@Ignore
	@Test
	public void testBothDynammicStaticHeadersKey() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> testMap = new HashMap<String, String>();
		testMap.put("x-Foo", "Bar");
		APICall<Void, Map<String,Object>> testCall = testInterface.getBothSingleParamStaticHeadersCall(testMap);
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		Assert.assertTrue(((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-foo"));
		Assert.assertTrue(((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-ping"));
		Assert.assertTrue(((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-static"));
		
	}
	
	@Test
	public void testBothDynammicStaticHeadersValue() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> testMap = new HashMap<String, String>();
		testMap.put("x-Foo", "Bar");
		APICall<Void, Map<String,Object>> testCall = testInterface.getBothSingleParamStaticHeadersCall(testMap);
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		
		Assert.assertEquals("Bar", ((Map<String,String>) response.getResponseBody().get("headers")).get("x-foo"));
		Assert.assertEquals("Pong", ((Map<String,String>) response.getResponseBody().get("headers")).get("x-ping"));
		Assert.assertEquals("True", ((Map<String,String>) response.getResponseBody().get("headers")).get("x-static"));
		
	}
	
	@Test
	public void testNoHeaders() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		Map<String,String> testMap = new HashMap<String, String>();
		testMap.put("x-Foo", "Bar");
		APICall<Void, Map<String,Object>> testCall = testInterface.noHeadersCall();
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		
		Assert.assertFalse(((Map<String,String>) response.getResponseBody().get("headers")).containsKey("x-foo"));
		
	}
	
	@Test
	public void testFailureDynamicHeader() throws Exception {
		
		APIHelper testAPIHelper = APIHelper.APIBuilder
				.builder("https://postman-echo.com")
				.build();		
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Header Parameters should be passed in Map");
		
		TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
		APICall<Void, Map<String,Object>> testCall = testInterface.incorrectHeadersCall("X-Foo:Bar");
		APICall<Void, Map<String,Object>> response = testCall.callMeNow();
		
	}

}
