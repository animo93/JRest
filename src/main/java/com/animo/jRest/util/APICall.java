package com.animo.jRest.util;

import com.animo.jRest.model.RequestBean;

import java.lang.reflect.Type;

/**
 * An invocation of a APIHelper method that sends a request to a webserver and returns a response.
 * Each call yields its own response object
 *
 * <p>Calls may be executed synchronously with {@link #callMeNow}, or asynchronously with {@link
 * #callMeLater}.
 *
 * @param <Response> Response type
 */
//TODO: Rename to APIRequest
public record APICall<Response>(RequestBean<Object> requestBean, Type responseType) {
    /**
     * Synchronous implementation of {@link com.animo.jRest.util.APICall APICall} , which invokes a blocking call to webserver
     * . And waits for the APICall to complete
     *
     * @return {@code APIResponse<Response>}
     * @throws Exception Exception if issue with asyncTask executeNow method
     */
    //TODO: Rename to execute
    public APIResponse<Response> callMeNow() throws Exception {

        final APIAsyncTask<Response> asyncTask = new APIAsyncTask<>(requestBean, responseType);
        return asyncTask.executeNow(requestBean);
    }

    //TODO: Add another method executeWithFuture and return a Future Object

    /**
     * Asynchronous implementation of {@link com.animo.jRest.util.APICall APICall} , which invokes a non-blocking call to webserver
     * . It accepts {@link APICallBack APICallBack} as a parameter
     *
     * @param callBack APICallBack
     */
    //TODO: Rename to executeWithCallback
    public void callMeLater(APICallBack<Response> callBack) {
        final APIAsyncTask<Response> asyncTask = new APIAsyncTask<>(requestBean, responseType, callBack);
        asyncTask.executeLater(requestBean, callBack);
    }

    ;
}
