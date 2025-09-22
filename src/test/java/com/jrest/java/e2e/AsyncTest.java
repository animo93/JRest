package com.jrest.java.e2e;

import com.jrest.java.api.APICallBack;
import com.jrest.java.api.APIResponse;
import com.jrest.java.api.JRest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;

public class AsyncTest {

    @Test
    public void testCallback_shouldInvokeCallBackOnSuccess() throws InterruptedException {
        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .build(TestPostmanEchoAPIInterface.class);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        APICallBack<Map<String, Object>> callback = Mockito.spy(new APICallBack<Map<String, Object>>() {
            @Override
            public void callBackOnSuccess(APIResponse<Map<String, Object>> result) {
                countDownLatch.countDown();
            }
            @Override
            public void callBackOnFailure(Throwable e) {
            }
        });
        testInterface.testCallback(callback);

        // To wait for the main thread to complete
        countDownLatch.await(3000, java.util.concurrent.TimeUnit.MILLISECONDS);

        Mockito.verify(callback).callBackOnSuccess(any());
        Mockito.verify(callback, Mockito.never()).callBackOnFailure(any());
    }

    @Test
    public void testCallback_shouldInvokeCallBackOnFailure_whenExecutionFails() throws InterruptedException {
        //Wrong url provided to simulate exception
        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com1")
                .build(TestPostmanEchoAPIInterface.class);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        APICallBack<Map<String, Object>> callback = Mockito.spy(new APICallBack<Map<String, Object>>() {
            @Override
            public void callBackOnSuccess(APIResponse<Map<String, Object>> result) {

            }
            @Override
            public void callBackOnFailure(Throwable e) {
                countDownLatch.countDown();
            }
        });
        testInterface.testCallback(callback);

        // To wait for the main thread to complete
        countDownLatch.await(3000, java.util.concurrent.TimeUnit.MILLISECONDS);

        Mockito.verify(callback,Mockito.never()).callBackOnSuccess(any());
        Mockito.verify(callback).callBackOnFailure(any());
    }

    @Test
    public void testFuture_shouldReturnFuture() throws Exception {
        final TestPostmanEchoAPIInterface testInterface = new JRest.APIBuilder("https://postman-echo.com")
                .build(TestPostmanEchoAPIInterface.class);
        Future<APIResponse<Map<String, Object>>> future = testInterface.testFuture();
        APIResponse<Map<String, Object>> response = future.get();

        assert(response != null);
        assert(response.responseCode() == 200);
    }
}
