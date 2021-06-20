package com.animo.jRest.test;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APIHelper;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class QueryParamTest {

    @Ignore
    @Test
    public void testSingleQueryParam() throws Exception {

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        String foo1 = "bar"+(new Random().nextInt(60000));
        final APICall<Void, Map<String, Object>> testCall = testInterface.getSingleQParamCall(foo1);
        final APICall<Void, Map<String, Object>> response = testCall.callMeNow();
        assertTrue(response.getResponseCode()==200);

    }

    @Ignore
    @Test
    public void testMultipleQueryParam() throws Exception {

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        String foo1 = "bar"+(new Random().nextInt(60000));
        String foo2 = "bar"+(new Random().nextInt(60000));
        final APICall<Void, Map<String, Object>> testCall = testInterface.getMultipleQParamCall(foo1, foo2);
        final APICall<Void, Map<String, Object>> response = testCall.callMeNow();
        assertTrue(response.getResponseCode()==200);

    }
    @Ignore
    @Test
    public void testQueryMap() throws Exception {

        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("foo1", "bar"+(new Random().nextInt(10)));
        paramMap.put("foo2", "bar"+(new Random().nextInt(10)));
        final APICall<Void, Map<String, Object>> testCall = testInterface.getQParamMapCall(paramMap);
        final APICall<Void, Map<String, Object>> response = testCall.callMeNow();
        assertTrue(response.getResponseCode()==200);

    }
}
