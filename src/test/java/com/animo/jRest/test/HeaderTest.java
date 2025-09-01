package com.animo.jRest.test;

import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.APIService;
import com.animo.jRest.util.APIResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class HeaderTest {

	@Test
	public void testSingleStaticHeaderKey() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final APIRequest<Map<String, Object>> testCall = testInterface.getCall();
		final APIResponse<Map<String, Object>> response = testCall.execute();
		assertTrue(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo"));
		
	}

	@Test
	public void testSingleStaticHeaderValue() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final APIRequest<Map<String, Object>> testCall = testInterface.getCall();
		final APIResponse<Map<String, Object>> response = testCall.execute();
		assertEquals("Bar", ((Map<String, String>) response.getResponse().get("headers")).get("x-foo"));
	}

	@Test
	public void testMultipleStaticHeaderKey() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final APIRequest<Map<String, Object>> testCall = testInterface.getMultipleHeadersCall();
		final APIResponse<Map<String, Object>> response = testCall.execute();
		assertTrue(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo") &&
				((Map<String, String>) response.getResponse().get("headers")).containsKey("x-ping"));
		
	}

	@Test
	public void testMultipleStaticHeaderValue() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final APIRequest<Map<String, Object>> testCall = testInterface.getMultipleHeadersCall();
		final APIResponse<Map<String, Object>> response = testCall.execute();
		assertEquals("Bar", ((Map<String, String>) response.getResponse().get("headers")).get("x-foo"));
		assertEquals("Pong", ((Map<String, String>) response.getResponse().get("headers")).get("x-ping"));
	}

	@Test
	public void testFailureStaticHeader() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		assertThrows(RuntimeException.class, () -> {
			final APIRequest<Map<String, Object>> testCall = testInterface.getIncorrectHeader();
		});
		
	}
	@Test
	public void testSingleDynamicHeaderKey() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");
		final APIRequest<Map<String, Object>> testCall = testInterface.getSingleParamHeadersCall(testMap);
		final APIResponse<Map<String, Object>> response = testCall.execute();
		assertTrue(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo"));
		
	}

	@Test
	public void testSingleDynamicHeaderValue() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");
		final APIRequest<Map<String, Object>> testCall = testInterface.getSingleParamHeadersCall(testMap);
		final APIResponse<Map<String, Object>> response = testCall.execute();
		
		assertEquals("Bar", ((Map<String, String>) response.getResponse().get("headers")).get("x-foo"));
		
	}

	@Test
	public void testBothDynamicStaticHeadersKey() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");
		final APIRequest<Map<String, Object>> testCall = testInterface.getBothSingleParamStaticHeadersCall(testMap);
		final APIResponse<Map<String, Object>> response = testCall.execute();
		assertTrue(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo"));
		assertTrue(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-ping"));
		assertTrue(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-static"));
		
	}
	
	@Test
	public void testBothDynamicStaticHeadersValue() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");
		final APIRequest<Map<String, Object>> testCall = testInterface.getBothSingleParamStaticHeadersCall(testMap);
		final APIResponse<Map<String, Object>> response = testCall.execute();
		
		assertEquals("Bar", ((Map<String, String>) response.getResponse().get("headers")).get("x-foo"));
		assertEquals("Pong", ((Map<String, String>) response.getResponse().get("headers")).get("x-ping"));
		assertEquals("True", ((Map<String, String>) response.getResponse().get("headers")).get("x-static"));
		
	}
	
	@Test
	public void testNoHeaders() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");
		final APIRequest<Map<String, Object>> testCall = testInterface.noHeadersCall();
		final APIResponse<Map<String, Object>> response = testCall.execute();
		
		assertFalse(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo"));
		
	}
	
	@Test
	public void testFailureDynamicHeader() throws Exception {
		
		final APIService testAPIService = APIService.APIBuilder
				.builder("https://postman-echo.com")
				.build();
		
		final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
		assertThrows(RuntimeException.class, () -> {
			final APIRequest<Map<String, Object>> testCall = testInterface.incorrectHeadersCall("X-Foo:Bar");
		});
		
	}

}
