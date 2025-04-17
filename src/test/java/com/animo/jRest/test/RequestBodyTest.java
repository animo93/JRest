package com.animo.jRest.test;

import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.APIHelper;
import com.animo.jRest.util.APIResponse;
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

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        final APIRequest<Map<String, Object>> testCall = testInterface.requestBodyObjectCall(testRequestBody);
        final APIResponse<Map<String, Object>> response = testCall.callMeNow();
        assertTrue(((Map<String, String>) response.getResponse().get("data")).containsKey("message"));

    }

    @Test
    public void testRequestBodyAsNull() throws Exception {

        TestRequestBody testRequestBody = null;

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        assertThrows(NullPointerException.class, () -> testInterface.requestBodyObjectCall(testRequestBody));

    }

    @Test
    public void testRequestBodyAsMap() throws Exception {

        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("foo","bar");

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        final APIRequest<Map<String, Object>> testCall = testInterface.requestBodyMapCall(requestMap);
        final APIResponse<Map<String, Object>> response = testCall.callMeNow();
        assertTrue(((Map<String, String>) response.getResponse().get("data")).containsKey("foo"));

    }
}
