package com.animo.jRest.test;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APIHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
/*
A Web server needs to be running on port 8081 for executing these below tests
 */

public class TestHttpRESTApi {

    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startServer() {
        mockServer = startClientAndServer(8081);
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }

    private void createExpectationForInvalidAuth() {
        new MockServerClient("localhost", 8081)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/get"),
                                //.withHeader("\"Content-type\", \"application/json\""),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                //.withBody("{ message: 'Success' }")
                                .withDelay(TimeUnit.SECONDS,1)
                );
    }

    @Test
    public void testSingleStaticHeaderKey() throws Exception {

        createExpectationForInvalidAuth();
        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("http://localhost:8081")
                .build();
        final HttpApiInterface testInterface = testAPIHelper.createApi(HttpApiInterface.class);
        final APICall<Void, Map<String, Object>> testCall = testInterface.getCall();
        final APICall<Void, Map<String, Object>> response = testCall.callMeNow();
        assertEquals(200,response.getResponseCode());

    }
}
