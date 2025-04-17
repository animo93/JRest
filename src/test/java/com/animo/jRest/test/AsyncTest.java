package com.animo.jRest.test;

import com.animo.jRest.util.APIRequest;
import com.animo.jRest.util.APICallBack;
import com.animo.jRest.util.APIHelper;
import com.animo.jRest.util.APIResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

public class AsyncTest {

    @Test
    public void callMeLater_shouldInvokeCallBackOnSuccess() throws InterruptedException {
        final APIHelper testAPIHelper = APIHelper.APIBuilder
                .builder("https://postman-echo.com")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        final APIRequest<Map<String, Object>> testCall = testInterface.getCall();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        APICallBack<Map<String, Object>> myCallBack = Mockito.spy(new APICallBack<Map<String, Object>>() {
            @Override
            public void callBackOnSuccess(APIResponse<Map<String, Object>> result) {
                countDownLatch.countDown();
            }
            @Override
            public void callBackOnFailure(Throwable e) {
            }
        });
        testCall.executeWithCallBack(myCallBack);

        // To wait for the main thread to complete
        countDownLatch.await();

        Mockito.verify(myCallBack).callBackOnSuccess(any());
        Mockito.verify(myCallBack, Mockito.never()).callBackOnFailure(any());
    }

    @Test
    public void callMeLater_shouldInvokeCallBackOnFailure_whenExecutionFails() throws InterruptedException {
        final APIHelper testAPIHelper = APIHelper.APIBuilder
                //Wrong url provided to simulate exception
                .builder("https://postman-echo.com1")
                .build();
        final TestPostmanEchoAPIInterface testInterface = testAPIHelper.createApi(TestPostmanEchoAPIInterface.class);
        final APIRequest<Map<String, Object>> testCall = testInterface.getCall();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        APICallBack<Map<String, Object>> myCallBack = Mockito.spy(new APICallBack<Map<String, Object>>() {
            @Override
            public void callBackOnSuccess(APIResponse<Map<String, Object>> result) {

            }
            @Override
            public void callBackOnFailure(Throwable e) {
                countDownLatch.countDown();
            }
        });
        testCall.executeWithCallBack(myCallBack);

        // To wait for the main thread to complete
        countDownLatch.await();

        Mockito.verify(myCallBack,Mockito.never()).callBackOnSuccess(any());
        Mockito.verify(myCallBack).callBackOnFailure(any());
    }
}
