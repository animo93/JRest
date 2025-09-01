package com.animo.jRest.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
/*
A Web server needs to be running on port 8081 for executing these below tests
 */

public class TestHttpRESTApi {

    /*private static ClientAndServer mockServer;

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
        final APIRequest<Map<String, Object>> testCall = testInterface.getCall();
        final APIResponse<Map<String, Object>> response = testCall.execute();
        assertEquals(200,response.getResponseCode());

    }*/
}
