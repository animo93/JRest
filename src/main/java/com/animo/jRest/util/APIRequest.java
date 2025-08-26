package com.animo.jRest.util;

import com.animo.jRest.model.RequestBean;

import java.lang.reflect.Type;
import java.util.concurrent.Future;

/**
 * An invocation of a APIHelper method that sends a request to a webserver and returns a response.
 * Each call yields its own response object
 *
 * <p>Calls may be executed synchronously with {@link #execute}, or asynchronously with {@link
 * #executeWithCallBack(APICallBack)} and {@link #executeWithFuture()}.
 *
 * @param requestBean
 * @param responseType
 */
public record APIRequest<Response>(RequestBean<Object> requestBean, Type responseType) {
    /**
     * Synchronous implementation of {@link APIRequest APIRequest} , which invokes a blocking call to webserver
     * . And waits for the APIRequest to complete
     *
     * @return {@link APIResponse<Response>}
     * @throws Exception Exception if issue with asyncTask executeNow method
     */
    public APIResponse<Response> execute() throws Exception {

        final APIClient<Response> client = new RESTAdapter<>(requestBean,responseType);
        return client.fetch();
    }

    //TODO: Add test cases
    /**
     * Asynchronous implementation of {@link APIRequest APIRequest} via Future.
     * @return a Future of {@link APIResponse<Response>}
     */
    public Future<APIResponse<Response>> executeWithFuture() {
        final APIClient<Response> client = new RESTAdapter<>(requestBean,responseType);
        return client.fetchWithFuture();
    }

    /**
     * Asynchronous implementation of {@link APIRequest APIRequest} , which invokes a non-blocking call to webserver
     * . It accepts {@link APICallBack APICallBack} as a parameter
     *
     * @param callBack APICallBack
     */
    public void executeWithCallBack(APICallBack<Response> callBack) {
        
        final APIClient<Response> client = new RESTAdapter<>(requestBean,responseType);
        client.fetchWithCallBack(callBack);
    }

    ;
}
