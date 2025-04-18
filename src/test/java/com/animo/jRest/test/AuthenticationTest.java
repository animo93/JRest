package com.animo.jRest.test;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APIHelper;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class AuthenticationTest {

    @Test
    public void testUsernamePasswordAuthentication() throws Exception {
        APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .addUsernameAndPassword("username", "password")
                .build();
        TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        APICall<Void, Map<String, Object>> testCall = testInterface.getCall();
        APICall<Void, Map<String, Object>> response = testCall.callMeNow();

        final String encodedCredentials = Base64.encodeBase64String("username:password".getBytes());
        Assertions.assertEquals("Basic "+encodedCredentials,((Map<String, String>)response.getResponseBody().get("headers")).get("authorization"));
    }
}
