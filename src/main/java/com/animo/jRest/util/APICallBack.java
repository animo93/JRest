package com.animo.jRest.util;

/**
 * Communicates responses from a server or offline requests. One and only one method will be invoked
 * in response to a given request.
 * Callbacks are only used when opting for performing an asynchronous non blocking call
 * @author animo
 *
 * @param <Response> Result type
 */

public interface APICallBack<Response> {
	/**
	 * Invoked for a received HTTP response
	 * <p>Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
	 * param myCall APICall parameter used for callback
	 */
    void callBackOnSuccess(APIResponse<Response> response);

	/**
	 * Invoked when a network exception occurred talking to the server or when an unexpected exception
	 * occurred creating the request or processing the response.
	 *  e Exception to be returned back
	 */
	void callBackOnFailure(Throwable e);
}
