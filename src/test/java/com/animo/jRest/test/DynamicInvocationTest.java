package com.animo.jRest.test;

import com.animo.jRest.util.APIResponse;
import com.animo.jRest.util.JRest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class DynamicInvocationTest {

    @Test
    public void testDynamicInvocation_noHeadersCall() throws Exception {
        DynamicInvocationTestInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .buildDynamic(DynamicInvocationTestInterface.class,"noHeadersCall");

        APIResponse<Map<String,Object>> response = (APIResponse<Map<String, Object>>) testInterface.dynamicAPIInvocation();

        assertFalse(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo"));
    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCall() throws Exception {
        DynamicInvocationTestInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .buildDynamic(DynamicInvocationTestInterface.class,
                        "bothQueryAndQueryMapCall",Map.class,String.class);

        Map<String,String> queryMap = new HashMap<String,String>();
        queryMap.put("foo", "bar");

        APIResponse<Map<String,Object>> response = (APIResponse<Map<String, Object>>) testInterface.dynamicAPIInvocation(queryMap,"pong");

        assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
        assertEquals("pong", ((Map<String,String>) response.getResponse().get("args")).get("ping"));
    }

    @Test
    public void testDynamicInvocationForInvalidMethod() throws Exception {
        Assertions.assertThrows(NoSuchMethodException.class,() -> {
            final DynamicInvocationTestInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                    .buildDynamic(DynamicInvocationTestInterface.class,"test");

            APIResponse<Map<String,Object>> response = (APIResponse<Map<String, Object>>) testInterface.dynamicAPIInvocation();
        });

    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCallWithResponse() throws Exception {
        DynamicInvocationTestInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .buildDynamic(DynamicInvocationTestInterface.class,
                        "bothQueryAndQueryMapCallWithResponse",Map.class,String.class);

        Map<String,String> queryMap = new HashMap<String,String>();
        queryMap.put("foo", "bar");

        APIResponse<TestAPIResponse> response = (APIResponse<TestAPIResponse>) testInterface.dynamicAPIInvocation(queryMap,"pong");

        assertEquals("bar",
                response.getResponse().getArgs().get("foo"));
        assertEquals("pong",
                response.getResponse().getArgs().get("ping"));
    }
}
