package com.animo.jRest.test;

import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.APIHelper;
import com.animo.jRest.util.APIResponse;
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
        APIRequest<Map<String, Object>> testCall = testInterface.getCall();
        APIResponse<Map<String, Object>> response = testCall.execute();

        final String encodedCredentials = Base64.encodeBase64String("username:password".getBytes());
        Assertions.assertEquals("Basic "+encodedCredentials,((Map<String, String>)response.getResponse().get("headers")).get("authorization"));
    }
}
