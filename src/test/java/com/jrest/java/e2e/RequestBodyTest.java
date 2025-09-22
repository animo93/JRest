package com.jrest.java.e2e;

import com.jrest.java.api.APIResponse;
import com.jrest.java.api.JRest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RequestBodyTest {

    @Test
    public void testRequestBodyAsObject() throws Exception {

        TestRequestBody testRequestBody = new TestRequestBody();
        testRequestBody.setBody("foo");
        testRequestBody.setMessage("bar");

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .build(TestPostmanEchoAPIInterface.class);

        final APIResponse<Map<String, Object>> response = testInterface.requestBodyObjectCall(testRequestBody);
        assertTrue(((Map<String, String>) response.response().get("data")).containsKey("message"));

    }

    @Test
    public void testRequestBodyAsNull() throws Exception {

        TestRequestBody testRequestBody = null;

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .build(TestPostmanEchoAPIInterface.class);

        assertThrows(NullPointerException.class, () -> testInterface.requestBodyObjectCall(testRequestBody));

    }

    @Test
    public void testRequestBodyAsMap() throws Exception {

        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("foo","bar");

        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .build(TestPostmanEchoAPIInterface.class);

        final APIResponse<Map<String, Object>> response = testInterface.requestBodyMapCall(requestMap);
        assertTrue(((Map<String, String>) response.response().get("data")).containsKey("foo"));

    }
}
