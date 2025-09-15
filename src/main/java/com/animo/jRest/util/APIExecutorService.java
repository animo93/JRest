package com.animo.jRest.util;

import com.animo.jRest.model.APIRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * An invocation of a APIService method that sends a request to a webserver and returns a response.
 * Each call yields its own response object
 *
 * <p>Calls may be executed synchronously with {@link #execute}, or asynchronously with {@link
 * #executeWithCallBack(APICallBack)} and {@link #executeWithFuture()}.
 *
 */
public final class APIExecutorService {

    final APIRequest apiRequest;

    public APIExecutorService(APIRequest apiRequest) {
        this.apiRequest = apiRequest;
    }

    /**
     * If the return type is of type APIResponse , execute synchronously
     * If the return type is of type Future<APIResponse> , execute asynchronously and return a Future
     * If the return type is of type void and has APICallBack as parameter , execute asynchronously and return via callback
     * **/
    public Optional<?> executeAPI() throws Exception {
        final Type returnType = apiRequest.returnType();
        if(returnType instanceof ParameterizedType pType) {
            if(pType.getRawType().equals(APIResponse.class)) {
                return Optional.of(execute());
            }else if(pType.getRawType().equals(Future.class)) {
                return Optional.of(executeWithFuture());
            }else {
                throw new IllegalArgumentException("Method return type must be of type APIResponse or Future<APIResponse>");
            }
        }else if(returnType.getTypeName().equals("void")) {
            executeWithCallBack(apiRequest.callBack().orElseThrow(() -> new IllegalArgumentException("APICallBack parameter required for void return type")));
        }else {
            throw new IllegalArgumentException("Method return type must be of type APIResponse or Future<APIResponse>");
        }
        return Optional.empty();
    }

    /**
     * Synchronous implementation of {@link APIExecutorService APIRequest} , which invokes a blocking call to webserver
     * . And waits for the APIRequest to complete
     *
     * @return {@link APIResponse<>}
     * @throws Exception Exception if issue with asyncTask executeNow method
     */
    private <Response> APIResponse<Response> execute() throws Exception {

        final APIClient client = new RESTClient(apiRequest);
        return client.fetch();
    }

    //TODO: Add test cases
    /**
     * Asynchronous implementation of {@link APIExecutorService APIRequest} via Future.
     * @return a Future of {@link APIResponse}
     */
    private <Response> Future<APIResponse<Response>> executeWithFuture() {
        final APIClient client = new RESTClient(apiRequest);
        return client.fetchWithFuture();
    }

    /**
     * Asynchronous implementation of {@link APIExecutorService APIRequest} , which invokes a non-blocking call to webserver
     * . It accepts {@link APICallBack APICallBack} as a parameter
     *
     * @param callBack APICallBack
     */
    private <Response> void executeWithCallBack(APICallBack<Response> callBack) {
        
        final APIClient client = new RESTClient(apiRequest);
        client.fetchWithCallBack(callBack);
    }
}
