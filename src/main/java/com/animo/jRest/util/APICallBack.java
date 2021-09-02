package com.animo.jRest.util;

/**
 * Communicates responses from a server or offline requests. One and only one method will be invoked
 * in response to a given request.
 * Callbacks are only used when opting for performing an asynchronous non blocking call
 * @author animo
 *
 * @param <Request> Request Type
 * @param <Response> Response Type
 */

public interface APICallBack<Request, Response> {
	/**
	 * Invoked for a received HTTP response
	 * <p>Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
	 * @param myCall APICall parameter used for callback
	 */
    void callBackOnSuccess(APICall<Request, Response> myCall);

	/**
	 * Invoked when a network exception occurred talking to the server or when an unexpected exception
	 * occurred creating the request or processing the response.
	 * @param e Exception to be returned back
	 */
	void callBackOnFailure(Exception e);
}
