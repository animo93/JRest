package com.animo.jRest.test;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APIHelper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class RequestBodyTest {

    @Test
    public void testRequestBodyAsObject() throws Exception {

        TestRequestBody testRequestBody = new TestRequestBody();
        testRequestBody.setBody("foo");
        testRequestBody.setMessage("bar");

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        final APICall<TestRequestBody, Map<String, Object>> testCall = testInterface.requestBodyObjectCall(testRequestBody);
        final APICall<TestRequestBody, Map<String, Object>> response = testCall.callMeNow();
        assertTrue(((Map<String, String>) response.getResponseBody().get("data")).containsKey("message"));

    }

    @Test
    public void testRequestBodyAsMap() throws Exception {

        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("foo","bar");

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        final APICall<Void, Map<String, Object>> testCall = testInterface.requestBodyMapCall(requestMap);
        final APICall<Void, Map<String, Object>> response = testCall.callMeNow();
        assertTrue(((Map<String, String>) response.getResponseBody().get("data")).containsKey("foo"));

    }
}
