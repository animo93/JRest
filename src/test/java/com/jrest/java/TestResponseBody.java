package com.jrest.java;

import com.jrest.java.api.APIResponse;
import com.jrest.java.api.JRest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestResponseBody {

    //TODO: Add wiremock support for testing this
    @Test
    public void testResponseBodyAsString() throws Exception {
        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .build(TestPostmanEchoAPIInterface.class);

        final APIResponse<String> response = testInterface.responseAsString();
        assertNotNull(response.response());
    }
}
