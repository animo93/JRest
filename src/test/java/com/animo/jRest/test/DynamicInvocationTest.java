package com.animo.jRest.test;

import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.APIService;
import com.animo.jRest.util.APIResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class DynamicInvocationTest {

    @Test
    public void testDynamicInvocation_noHeadersCall() throws Exception {
        DynamicInvocationTestInterface testInterface = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .buildDynamic(DynamicInvocationTestInterface.class,"noHeadersCall");

        APIRequest<Map<String,Object>> call = testInterface.dynamicAPIInvocation();
        APIResponse<Map<String,Object>> response = call.execute();

        assertFalse(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo"));
    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCall() throws Exception {
        DynamicInvocationTestInterface testInterface = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .buildDynamic(DynamicInvocationTestInterface.class,
                        "bothQueryAndQueryMapCall",Map.class,String.class);

        Map<String,String> queryMap = new HashMap<String,String>();
        queryMap.put("foo", "bar");

        APIRequest<Map<String,Object>> call = testInterface.dynamicAPIInvocation(queryMap,"pong");
        APIResponse<Map<String,Object>> response = call.execute();

        assertEquals("bar", ((Map<String,String>) response.getResponse().get("args")).get("foo"));
        assertEquals("pong", ((Map<String,String>) response.getResponse().get("args")).get("ping"));
    }

    @Test
    public void testDynamicInvocationForInvalidMethod() throws Exception {
        Assertions.assertThrows(NoSuchMethodException.class,() -> {
            final DynamicInvocationTestInterface testInterface = APIService.APIBuilder
                    .builder("https://postman-echo.com")
                    .buildDynamic(DynamicInvocationTestInterface.class,"test");
            APIRequest<Map<String,Object>> call = testInterface.dynamicAPIInvocation();
            APIResponse<Map<String,Object>> response = call.execute();
        });

    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCallWithResponse() throws Exception {
        DynamicInvocationTestResponseInterface testInterface = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .buildDynamic(DynamicInvocationTestResponseInterface.class,
                        "bothQueryAndQueryMapCallWithResponse",Map.class,String.class);

        Map<String,String> queryMap = new HashMap<String,String>();
        queryMap.put("foo", "bar");

        APIRequest<TestAPIResponse> call = testInterface.dynamicAPIInvocation(queryMap,"pong");
        APIResponse<TestAPIResponse> response = call.execute();

        assertEquals("bar",
                response.getResponse().getArgs().get("foo"));
        assertEquals("pong",
                response.getResponse().getArgs().get("ping"));
    }
}
