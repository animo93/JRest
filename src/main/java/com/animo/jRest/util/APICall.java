package com.animo.jRest.util;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.animo.jRest.model.RequestBean;
import lombok.Getter;
import lombok.Setter;

/**
 * An invocation of a APIHelper method that sends a request to a webserver and returns a response.
 * Each call yields its own response object
 *
 * <p>Calls may be executed synchronously with {@link #callMeNow}, or asynchronously with {@link
 * #callMeLater}.
 * @param <Response> Response type
 */

@Getter
@Setter
public class APICall<Response> {
    private Response responseBody;
    private int responseCode;
    private RequestBean<Object> requestBean;
    private Map<String, List<String>> responseHeaders;
    private Type responseType;

    /**
     * Synchronous implementation of {@link com.animo.jRest.util.APICall APICall} , which invokes a blocking call to webserver
     * . And waits for the APICall to complete
     *
     * @return {@code APIResponse<Response>}
     * @throws Exception Exception if issue with asyncTask executeNow method
     */
    //TODO: A new return type should be introduced here , not the same as the class APICall
    public APIResponse<Response> callMeNow() throws Exception {

        final APIAsyncTask<Response> asyncTask = new APIAsyncTask<>(requestBean, responseType);
        return asyncTask.executeNow(requestBean);
    }
    
    /**
     * Asynchronous implementation of {@link com.animo.jRest.util.APICall APICall} , which invokes a non-blocking call to webserver
     * . It accepts {@link com.animo.jRest.util.APICallBack APICallBack} as a parameter
     * @param callBack APICallBack
     */
    public void callMeLater(APICallBack<Response> callBack) {
        final APIAsyncTask<Response> asyncTask = new APIAsyncTask<>(requestBean, responseType, callBack);
        asyncTask.executeLater(requestBean,callBack);
    };
}
