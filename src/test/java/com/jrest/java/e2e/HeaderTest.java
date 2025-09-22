package com.jrest.java.e2e;

import com.jrest.java.api.JRest;
import com.jrest.java.api.APIResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class HeaderTest {

	@Test
	public void testSingleStaticHeaderKey() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final APIResponse<Map<String, Object>> response = testInterface.getCall();
		assertTrue(((Map<String, String>) response.response().get("headers")).containsKey("x-foo"));
		
	}

	@Test
	public void testSingleStaticHeaderValue() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		final APIResponse<Map<String, Object>> response = testInterface.getCall();
		assertEquals("Bar", ((Map<String, String>) response.response().get("headers")).get("x-foo"));
	}

	@Test
	public void testMultipleStaticHeaderKey() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final APIResponse<Map<String, Object>> response = testInterface.getMultipleHeadersCall();
		//final APIResponse<Map<String, Object>> response = testCall.execute();
		assertTrue(((Map<String, String>) response.response().get("headers")).containsKey("x-foo") &&
				((Map<String, String>) response.response().get("headers")).containsKey("x-ping"));
		
	}

	@Test
	public void testMultipleStaticHeaderValue() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final APIResponse<Map<String, Object>> response = testInterface.getMultipleHeadersCall();
		//final APIResponse<Map<String, Object>> response = testCall.execute();
		assertEquals("Bar", ((Map<String, String>) response.response().get("headers")).get("x-foo"));
		assertEquals("Pong", ((Map<String, String>) response.response().get("headers")).get("x-ping"));
	}

	@Test
	public void testFailureStaticHeader() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		assertThrows(RuntimeException.class, () -> {
			final APIResponse<Map<String, Object>> testCall = testInterface.getIncorrectHeader();
		});
		
	}
	@Test
	public void testSingleDynamicHeaderKey() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");

		final APIResponse<Map<String, Object>> response = testInterface.getSingleParamHeadersCall(testMap);
		assertTrue(((Map<String, String>) response.response().get("headers")).containsKey("x-foo"));
		
	}

	@Test
	public void testSingleDynamicHeaderValue() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");

		final APIResponse<Map<String, Object>> response = testInterface.getSingleParamHeadersCall(testMap);
		
		assertEquals("Bar", ((Map<String, String>) response.response().get("headers")).get("x-foo"));
		
	}

	@Test
	public void testBothDynamicStaticHeadersKey() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");

		final APIResponse<Map<String, Object>> response = testInterface.getBothSingleParamStaticHeadersCall(testMap);
		assertTrue(((Map<String, String>) response.response().get("headers")).containsKey("x-foo"));
		assertTrue(((Map<String, String>) response.response().get("headers")).containsKey("x-ping"));
		assertTrue(((Map<String, String>) response.response().get("headers")).containsKey("x-static"));
		
	}
	
	@Test
	public void testBothDynamicStaticHeadersValue() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");

		final APIResponse<Map<String, Object>> response = testInterface.getBothSingleParamStaticHeadersCall(testMap);
		
		assertEquals("Bar", ((Map<String, String>) response.response().get("headers")).get("x-foo"));
		assertEquals("Pong", ((Map<String, String>) response.response().get("headers")).get("x-ping"));
		assertEquals("True", ((Map<String, String>) response.response().get("headers")).get("x-static"));
		
	}
	
	@Test
	public void testNoHeaders() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
		final Map<String, String> testMap = new HashMap<>();
		testMap.put("x-Foo", "Bar");

		final APIResponse<Map<String, Object>> response = testInterface.noHeadersCall();
		
		assertFalse(((Map<String, String>) response.response().get("headers")).containsKey("x-foo"));
		
	}
	
	@Test
	public void testFailureDynamicHeader() throws Exception {

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);

		assertThrows(RuntimeException.class, () -> {
			final APIResponse<Map<String, Object>> testCall = testInterface.incorrectHeadersCall("X-Foo:Bar");
		});
		
	}

}
