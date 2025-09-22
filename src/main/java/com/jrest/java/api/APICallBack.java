package com.jrest.java.api;

/**
 * Callback interface for delivering parsed responses.
 *
 * @param <Response> The type of the successful response object.
 */
public interface APICallBack<Response> {
	/**
	 * Invoked for a received HTTP response
	 * <p>Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * @param response APIResponse object which holds the response data
	 */
    void callBackOnSuccess(APIResponse<Response> response);

	/**
	 * Invoked when a network exception occurred talking to the server or when an unexpected exception
	 * occurred creating the request or processing the response.
	 * @param e Exception to be returned back
	 */
	void callBackOnFailure(Throwable e);
}
