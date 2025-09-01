package com.animo.jRest.test;

import com.animo.jRest.util.APIService;
import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.APIResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestResponseBody {

    //TODO: Add wiremock support for testing this
    @Test
    public void testResponseBodyAsString() throws Exception {
        final APIService testAPIService = APIService.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIService.createApi(TestPostmanEchoAPIInterface.class);
        final APIRequest<String> testCall = testInterface.responseAsString();
        final APIResponse<String> response = testCall.execute();
        assertNotNull(response.getResponse());
    }
}
