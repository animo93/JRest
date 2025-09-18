package com.animo.jRest.test;

import com.animo.jRest.util.JRest;
import com.animo.jRest.util.APIResponse;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class AuthenticationTest {

    @Test
    public void testUsernamePasswordAuthentication() throws Exception {
        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .addUsernameAndPassword("username", "password")
                .build(TestPostmanEchoAPIInterface.class);
        APIResponse<Map<String, Object>> response = testInterface.getCall();

        final String encodedCredentials = Base64.encodeBase64String("username:password".getBytes());
        Assertions.assertEquals("Basic "+encodedCredentials,((Map<String, String>)response.response().get("headers")).get("authorization"));
    }
}
