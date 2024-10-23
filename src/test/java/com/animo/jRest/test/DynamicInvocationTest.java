package com.animo.jRest.test;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APIHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class DynamicInvocationTest {

    @Test
    public void testDynamicInvocation_noHeadersCall() throws Exception {
        APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        DynamicInvocationTestInterface testInterface = testAPIHelper.createDynamicApi(DynamicInvocationTestInterface.class,"noHeadersCall");
        APICall<Void, Map<String,Object>> call = testInterface.dynamicAPIInvocation();
        APICall<Void,Map<String,Object>> response = call.callMeNow();

        assertFalse(((Map<String, String>) response.getResponseBody().get("headers")).containsKey("x-foo"));
    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCall() throws Exception {
        APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        DynamicInvocationTestInterface testInterface = testAPIHelper.createDynamicApi(DynamicInvocationTestInterface.class,
                "bothQueryAndQueryMapCall",Map.class,String.class);

        Map<String,String> queryMap = new HashMap<String,String>();
        queryMap.put("foo", "bar");

        APICall<Void, Map<String,Object>> call = testInterface.dynamicAPIInvocation(queryMap,"pong");
        APICall<Void,Map<String,Object>> response = call.callMeNow();

        assertEquals("bar", ((Map<String,String>) response.getResponseBody().get("args")).get("foo"));
        assertEquals("pong", ((Map<String,String>) response.getResponseBody().get("args")).get("ping"));
    }

    @Test
    public void testDynamicInvocationForInvalidMethod() throws Exception {
        APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        Assertions.assertThrows(NoSuchMethodException.class,() -> {
                DynamicInvocationTestInterface testInterface = testAPIHelper.createDynamicApi(DynamicInvocationTestInterface.class,"test");
                APICall<Void, Map<String,Object>> call = testInterface.dynamicAPIInvocation();
                APICall<Void,Map<String,Object>> response = call.callMeNow();
        });

    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCallWithResponse() throws Exception {
        APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        DynamicInvocationTestResponseInterface testInterface = testAPIHelper.createDynamicApi(DynamicInvocationTestResponseInterface.class,
                "bothQueryAndQueryMapCallWithResponse",Map.class,String.class);

        Map<String,String> queryMap = new HashMap<String,String>();
        queryMap.put("foo", "bar");

        APICall<Void, TestAPIResponse> call = testInterface.dynamicAPIInvocation(queryMap,"pong");
        APICall<Void,TestAPIResponse> response = call.callMeNow();

        assertEquals("bar",
                response.getResponseBody().getArgs().get("foo"));
        assertEquals("pong",
                response.getResponseBody().getArgs().get("ping"));
    }
}
