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
        APIService testAPIService = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        DynamicInvocationTestInterface testInterface = testAPIService.createDynamicApi(DynamicInvocationTestInterface.class,"noHeadersCall");
        APIRequest<Map<String,Object>> call = testInterface.dynamicAPIInvocation();
        APIResponse<Map<String,Object>> response = call.execute();

        assertFalse(((Map<String, String>) response.getResponse().get("headers")).containsKey("x-foo"));
    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCall() throws Exception {
        APIService testAPIService = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        DynamicInvocationTestInterface testInterface = testAPIService.createDynamicApi(DynamicInvocationTestInterface.class,
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
        APIService testAPIService = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        Assertions.assertThrows(NoSuchMethodException.class,() -> {
                DynamicInvocationTestInterface testInterface = testAPIService.createDynamicApi(DynamicInvocationTestInterface.class,"test");
                APIRequest<Map<String,Object>> call = testInterface.dynamicAPIInvocation();
                APIResponse<Map<String,Object>> response = call.execute();
        });

    }


    @Test
    public void testDynamicInvocation_bothQueryAndQueryMapCallWithResponse() throws Exception {
        APIService testAPIService = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .build();

        DynamicInvocationTestResponseInterface testInterface = testAPIService.createDynamicApi(DynamicInvocationTestResponseInterface.class,
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
