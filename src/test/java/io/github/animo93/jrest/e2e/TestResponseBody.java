package io.github.animo93.jrest.e2e;

import io.github.animo93.jrest.api.APIResponse;
import io.github.animo93.jrest.api.JRest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestResponseBody {

    //TODO: Add wiremock support for testing this
    //@Test
    public void testResponseBodyAsString() throws Exception {
        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .build(TestPostmanEchoAPIInterface.class);

        final APIResponse<String> response = testInterface.responseAsString();
        assertNotNull(response.response());
    }
}
